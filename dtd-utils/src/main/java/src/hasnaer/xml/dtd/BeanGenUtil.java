package hasnaer.xml.dtd;

import hasnaer.ds.tree.TreeNode;
import hasnaer.ds.tree.andor.AndOrTree;
import hasnaer.xml.dtd.JavaBean.Type;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 *
 * @author hasnae rehioui
 */
public class BeanGenUtil {

    public static ClassPool _pool;
    public static CtClass _javaBeanClass;
    public static Map<String, String> _genClassByNodeName;

    static {
        try {
            _genClassByNodeName = new HashMap<String, String>();
            _pool = ClassPool.getDefault();
            _javaBeanClass = _pool.get("hasnaer.xml.dtd.JavaBean");

        } catch (Exception ex) {
        }
    }

    static CtClass createInterface(String interfaceName, String packageName)
            throws Exception {
        _genClassByNodeName.put(interfaceName + "ENTITY",
                packageName + "." + makeJavaName(interfaceName));
        return _pool.makeInterface(packageName + "."
                + makeJavaName(interfaceName));
    }

    static String makeJavaName(String name) {
        StringBuilder _builder = new StringBuilder();
        String[] _tokens = name.split("\\-|:|\\.");

        for (int i = 0; i < _tokens.length; i++) {
            _builder.append(_tokens[i].substring(0, 1).toUpperCase());
            _builder.append(_tokens[i].substring(1));
        }
        return _builder.toString();
    }

    static String createEnumJavaStr(String name, String packageName,
            List<String> values) throws Exception {
        _genClassByNodeName.put(name + "ENTITY",
                packageName + "." + makeJavaName(name));
        return new EnumTemplate(packageName,
                makeJavaName(name), values).content;
    }

    static void loadClass(FileInputStream stream) throws Exception {
        _pool.makeClass(stream);
    }

    static CtClass createClass(String javaName, String name,
            String packageName, Type type) throws Exception {

//        // System.err.println("** createClass: " + javaName);
        _genClassByNodeName.put(name + "ELEMENT", packageName + "." + javaName);
        CtClass _class = _pool.makeClass(packageName + "."
                + javaName, _javaBeanClass);

        
        // add default constructor
        StringBuilder _builder = new StringBuilder("public ");
        _builder.append(javaName).append("(){super(hasnaer.xml.dtd.JavaBean.Type.");
        _builder.append(type.toString()).append(", \"").append(name).append("\");}");
//        // System.err.println("** constructor: " + _builder.toString());
        _class.addConstructor(CtNewConstructor.make(_builder.toString(), _class));
        return _class;
    }

    static CtClass findInterface(String _interface) throws Exception {
        return _pool.get(_interface);
    }

    static void addGroupNodesToClass(AndOrTree childNodes,
            String packageName, CtClass javaClass, ArrayList<String> names)
            throws Exception {

        // System.err.println("childNodes tree: " + childNodes);
        List<TreeNode> _nodes = childNodes._nodes;
        for (TreeNode _node : _nodes) {
            if (_node instanceof AndOrTree) {
                
                addGroupNodesToClass((AndOrTree) _node, packageName,
                        javaClass, names);

            } else if (_node instanceof ChildNode) {
                
                ChildNode _child = (ChildNode) _node;
                
                if (!names.contains(_child._value)) {
                    // System.err.println("adding " + makeJavaName(_child._value));
                    if (_child._type.isData()) {
                        addChildToClass("String", _child._value, javaClass);
                    } else if (_child._type.isElement()) {

                        addChildToClass(_genClassByNodeName.get(_child._value
                                + "ELEMENT"), makeJavaName(_child._value), javaClass);

                    } else if (_child._type.isEntity()) {
                        addChildToClass(_genClassByNodeName.get(_child._value
                                + "ENTITY"), makeJavaName(_child._value),
                                javaClass);
                    }
                    names.add(_child._value);
                }
            }
        }

    }

    static void addAttributeAccessToClass(Attribute _attribute,
            String packageName, CtClass javaClass, String outputDir)
            throws Exception {

        String _attJavaName = BeanGenUtil.makeJavaName(_attribute._name);
        if (_attribute._type == Attribute.Type.ENUM) {

            if (_attribute._valuesFromEntity == null) {
                _genClassByNodeName.put(_attribute._name + "ENTITY",
                        packageName + ".types." + _attJavaName);

                String _mainDirectory = outputDir + "/"
                        + packageName.replace('.', '/') + "/types";
                new File(_mainDirectory).mkdirs();
                String _enumJavaStr = BeanGenUtil.createEnumJavaStr(
                        _attJavaName, packageName + ".types",
                        _attribute._values);

                File _enumJavaFile = new File(_mainDirectory
                        + "/" + _attJavaName + ".java");
                FileOutputStream _fos = new FileOutputStream(_enumJavaFile);
                _fos.write(_enumJavaStr.getBytes());
                _fos.close();

                JavaCompiler _javac = ToolProvider.getSystemJavaCompiler();
                _javac.run(null, System.out, System.err, new String[]{
                            _enumJavaFile.getAbsolutePath()});

                File _enumClassFile = new File(_mainDirectory + "/"
                        + _attJavaName + ".class");
                _enumJavaFile.delete();

                BeanGenUtil.loadClass(new FileInputStream(_mainDirectory + "/"
                        + _enumClassFile.getName()));
            }
        }

        StringBuilder _builder = new StringBuilder("public final void setATT_");
        _builder.append(_attJavaName).append("(");
        if (_attribute._dvstatus == Attribute.DVStatus.FIXED) {
            _builder.append("){this.setAttribute(\"").append(_attribute._name);
            _builder.append("\",\"").append(_attribute._defaultValue);
            _builder.append("\");}");
        } else {
            if (_attribute._type == Attribute.Type.ENUM) {
                String _typeName = (_attribute._valuesFromEntity != null)
                        ? _attribute._valuesFromEntity : _attribute._name;
                _builder.append(_genClassByNodeName.get(
                        _typeName + "ENTITY"));

                _builder.append(" ");
            } else {
                _builder.append("String ");
            }

            _builder.append(" value){this.setAttribute(\"").append(_attribute._name);
            _builder.append("\", value);}");
        }



        javaClass.addMethod(CtMethod.make(_builder.toString(), javaClass));
    }

    private static void addChildToClass(String type, String name,
            CtClass javaClass) throws Exception {

        StringBuilder _builder = new StringBuilder("public final void append_");
        _builder.append(name).append("(").append(type);
        _builder.append(" value){this.appendChild(value);}");
        javaClass.addMethod(CtMethod.make(_builder.toString(), javaClass));
    }

    private static class EnumTemplate {

        String content;

        EnumTemplate(String packageName, String name, List<String> values) {
            this.content = make(packageName, name, values);
        }

        private String make(String packageName, String name, List<String> values) {
            StringBuilder _builder = new StringBuilder("package ");
            _builder.append(packageName);
            _builder.append(";\n");
            _builder.append("public enum ");
            _builder.append(makeJavaName(name));
            _builder.append(" {\n");

            _builder.append("_").append(makeJavaName(values.get(0)).toUpperCase());
            _builder.append("(\"");
            _builder.append(values.get(0));
            _builder.append("\")");

            for (int i = 1; i < values.size(); i++) {
                _builder.append(",");
                _builder.append("_").append(makeJavaName(values.get(i)).toUpperCase());
                _builder.append("(\"");
                _builder.append(values.get(i));
                _builder.append("\")");
            }

            _builder.append(";");
            _builder.append("String value;\n ");
            _builder.append(makeJavaName(name));
            _builder.append(" (String v){this.value = v;}");
            _builder.append("public String toString(){return this.value;}");
            _builder.append("}");
            return _builder.toString();
        }
    }
}