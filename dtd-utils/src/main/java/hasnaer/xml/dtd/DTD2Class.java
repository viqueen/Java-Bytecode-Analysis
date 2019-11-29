package hasnaer.xml.dtd;

import hasnaer.xml.dtd.Entity.Type;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javassist.CtClass;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 *
 * @author hasnae rehioui
 */
public class DTD2Class {

    public static void execute(String dtdFileName, String elementPrefix,
            String rootPackage, String outputDir, List<DTD> dependencies)
            throws Exception {
        execute(new FileInputStream(dtdFileName), elementPrefix, rootPackage,
                outputDir, dependencies);
    }

    public static void execute(File dtdFile, String elementPrefix,
            String rootPackage, String outputDir, List<DTD> dependencies)
            throws Exception {
        execute(new FileInputStream(dtdFile), elementPrefix, rootPackage,
                outputDir, dependencies);
    }

    public static void execute(InputStream dtdStream, String elementPrefix,
            String rootPackage, String outputDir, List<DTD> dependencies)
            throws Exception {

        execute(new DTD(rootPackage, dtdStream, dependencies, elementPrefix),
                outputDir);

    }

    public static void execute(DTD dtd, String outputDir) throws Exception {
        // System.err.println("****** DTD2CLASS START");

        // render entities
        // System.err.println("-- Render ENTITIES");
        List<Entity> _entityQueue = new ArrayList<Entity>();
        for(Entry<String, Entity> _entry : dtd._entities.entrySet()){
            if(_entry.getValue().isReady()){
                _entityQueue.add(_entry.getValue());
            }
        }
        while(!_entityQueue.isEmpty()){
            Entity _entity = _entityQueue.remove(0);
            entity2class(_entity, outputDir);
            for(String _child : _entity._childInterfaces){
                Entity _childEntity = dtd._entities.get(_child);
                if(_childEntity.isReady()){
                    _entityQueue.add(_childEntity);
                }
            }
        }


//        for (Entry<String, Entity> _entry : dtd._entities.entrySet()) {
//            entity2class(_entry.getValue(), outputDir);
//        }

        // render elements
        // System.err.println("-- Render ELEMENTS");
        List<Element> _elementsQueue = new ArrayList<Element>();
        for (Entry<String, Element> _entry : dtd._elements.entrySet()) {
            if (_entry.getValue().isReady()) {
                _elementsQueue.add(_entry.getValue());
            }
        }
        while (!_elementsQueue.isEmpty()) {
            Element _element = _elementsQueue.remove(0);
            element2class(_element, outputDir);
            for (Element _parent : _element._parents) {
                if (_parent.isReady()) {
                    _elementsQueue.add(_parent);
                }
            }
        }

        // System.err.println("****** DTD2CLASS STOP");
    }

    private static void entity2class(Entity entity, String outputDir)
            throws Exception {
        if (!entity._rendered) {
            // System.err.println("--- considering: " + entity._name);
            String _mainDirectory = outputDir + "/"
                    + entity._rootPackage.replace('.', '/');

            if (entity._type == Entity.Type.ELEMENTS || entity._type == null
                    || entity._type == Entity.Type.VALUE) {
                // System.err.println("java interface");
                String _javaName = BeanGenUtil.makeJavaName(entity._name);
                CtClass _javaInterface = BeanGenUtil.createInterface(
                        entity._name, entity._rootPackage + ".spi");

                // add interfaces
                // System.err.println("** add parent interfaces");
                for (String _interface : entity._interfaces) {
                    // System.err.println(_interface);
                    _javaInterface.addInterface(BeanGenUtil.findInterface(
                            BeanGenUtil._genClassByNodeName.get(
                            _interface + "ENTITY")));
                }

                new File(_mainDirectory + "/spi").mkdirs();
                _javaInterface.toBytecode(new DataOutputStream(
                        new FileOutputStream(new File(_mainDirectory
                        + "/spi/" + _javaName + ".class"))));

                entity._rendered = true;

            } else if (entity._type == Type.VALUES) {
                // System.err.println("java enum: " + entity._values);
                String _javaName = BeanGenUtil.makeJavaName(entity._name);
                String _enumJavaStr = BeanGenUtil.createEnumJavaStr(
                        entity._name, entity._rootPackage + ".types",
                        entity._values);

                new File(_mainDirectory + "/types").mkdirs();
                File _enumJavaFile = new File(_mainDirectory
                        + "/types/" + _javaName + ".java");

                FileOutputStream _fos = new FileOutputStream(_enumJavaFile);
                _fos.write(_enumJavaStr.getBytes());
                _fos.close();

                JavaCompiler _javac = ToolProvider.getSystemJavaCompiler();
                _javac.run(null, System.out, System.err, new String[]{
                            _enumJavaFile.getAbsolutePath()
                        });

                File _enumClassFile = new File(_mainDirectory + "/types/"
                        + _javaName + ".class");

                _enumJavaFile.delete();

                BeanGenUtil.loadClass(new FileInputStream(_mainDirectory
                        + "/types/" + _enumClassFile.getName()));

                entity._rendered = true;
            } else {
                // System.err.println("none");
                entity._rendered = false;
            }

        }
    }

    private static void element2class(Element element, String outputDir)
            throws Exception {
        if (!element._rendered) {

            // System.err.println("--- considering: " + element._name);
            String _javaName = BeanGenUtil.makeJavaName(element._name);
            CtClass _javaClass = BeanGenUtil.createClass(_javaName,
                    element._prefix + element._name,
                    element._rootPackage + ".impl", element._category.toBeanType());

            // add interfaces
            // System.err.println("** add interfaces");
            for (String _interface : element._interfaces) {
                // System.err.println(_interface);
                _javaClass.addInterface(BeanGenUtil.findInterface(
                        BeanGenUtil._genClassByNodeName.get(
                        _interface + "ENTITY")));
            }

            // add childnodes
            // System.err.println("** add childNodes");
            BeanGenUtil.addGroupNodesToClass(element._childNodes,
                    element._rootPackage, _javaClass, new ArrayList<String>());

            // add attributes
            // System.err.println("** add attributes");
            for (Attribute _attribute : element._attributes) {
                // System.err.println(_attribute._name);
                BeanGenUtil.addAttributeAccessToClass(_attribute,
                        element._rootPackage, _javaClass, outputDir);
            }

            String _mainDirectory = outputDir + "/"
                    + element._rootPackage.replace('.', '/') + "/impl";

            new File(_mainDirectory).mkdirs();
            _javaClass.toBytecode(new DataOutputStream(
                    new FileOutputStream(new File(
                    _mainDirectory + "/" + _javaName + ".class"))));
            element._rendered = true;
        }
    }

    public static void main(String[] args) throws Exception {
        String _rootPackage = "hasnaer.xsd";
        String _outputDir = "output_xsd_v7";
        // System.err.println("@@@@@@@@@ XSD");
        DTD2Class.execute("xmlschema.dtd", "", _rootPackage, _outputDir, null);
    }
}