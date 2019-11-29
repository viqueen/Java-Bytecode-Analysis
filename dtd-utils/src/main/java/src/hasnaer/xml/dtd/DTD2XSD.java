package hasnaer.xml.dtd;

import hasnaer.data.util.IOUtils;
import hasnaer.ds.tree.TreeNode;
import hasnaer.ds.tree.andor.AndOrTree;
import hasnaer.xsd.XsDatatypes.impl.XsEnumeration;
import hasnaer.xsd.XsDatatypes.impl.XsRestriction;
import hasnaer.xsd.XsDatatypes.impl.XsSimpleType;
import hasnaer.xsd.impl.XsAttribute;
import hasnaer.xsd.impl.XsAttributeGroup;
import hasnaer.xsd.impl.XsChoice;
import hasnaer.xsd.impl.XsElement;
import hasnaer.xsd.impl.XsGroup;
import hasnaer.xsd.impl.XsSchema;
import hasnaer.xsd.impl.XsSequence;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author hasnae rehioui
 */
public class DTD2XSD {

    public static void execute(String dtdFileName) throws Exception {
        execute(dtdFileName, dtdFileName.replaceAll("\\.dtd", ".xsd"));
    }

    public static void execute(String dtdFileName, String xsdFileName)
            throws Exception {
        execute(new FileInputStream(dtdFileName),
                new FileOutputStream(xsdFileName));
    }

    public static void execute(File dtdFile, File xsdFile)
            throws Exception {
        execute(new FileInputStream(dtdFile), new FileOutputStream(xsdFile));
    }

    public static void execute(String dtdStream, OutputStream xsdStream)
            throws Exception {
                    DTD _dtd = new DTD(dtdStream, "");
                    try{

//            // System.err.println("Some Entity "+ _dtd._entities.get("ED-attrib.list")._type);
//            // System.err.println("Some Entity "+ _dtd._entities.get("ED-attrib.list")._attributes);
//
//            System.exit(0);


            XsSchema _schema = new XsSchema();
            _schema.setATT_XmlnsXs();

            // render entities
            // System.err.println("-- Render ENTITIES");
            List<Entity> _entityQueue = new ArrayList<Entity>();
            for (Entry<String, Entity> _entry : _dtd._entities.entrySet()) {
                if (_entry.getValue().isReady()) {
                    _entityQueue.add(_entry.getValue());
                }
            }
            while (!_entityQueue.isEmpty()) {
                Entity _entity = _entityQueue.remove(0);
                entity2schemaNode(_entity, _schema);
                for (String _child : _entity._childInterfaces) {
                    Entity _childEntity = _dtd._entities.get(_child);
                    if (_childEntity.isReady()) {
                        _entityQueue.add(_childEntity);
                    }
                }
            }

            // render elements
            // System.err.println("-- Render ELEMENTS");
            List<Element> _elementQueue = new ArrayList<Element>();
            for (Entry<String, Element> _entry : _dtd._elements.entrySet()) {
                if (_entry.getValue().isReady()) {
                    _elementQueue.add(_entry.getValue());
                }
            }
            while (!_elementQueue.isEmpty()) {
                Element _element = _elementQueue.remove(0);
                element2schemaNode(_element, _schema);
                for (Element _parent : _element._parents) {
                    if (_parent.isReady()) {
                        _elementQueue.add(_parent);
                    }
                }
            }

            // System.err.println(_schema.renderXMLstring());

            xsdStream.write(_schema.renderXMLstring().getBytes());

        } catch (Exception ex) {
            throw ex;
        } finally {
            
            xsdStream.close();
        }
    }
    
    public static void execute(InputStream dtdStream, OutputStream xsdStream)
            throws Exception {
        execute(IOUtils.stream2string(dtdStream), xsdStream);
    }

    public static void main(String[] args) throws Exception {
        String _dtdFileName = "cda10.dtd";
        DTD2XSD.execute(_dtdFileName);
    }

    private static void entity2schemaNode(Entity entity, XsSchema schema) {
        if (entity._type != null) {
            switch (entity._type) {
                case VALUES:
                    XsSimpleType _simpleType = new XsSimpleType();
                    _simpleType.setATT_Name(entity._name);

                    XsRestriction _restriction = new XsRestriction();
                    _restriction.setATT_Base(BASE_STRING);

                    for (String _value : entity._values) {
                        XsEnumeration _enumeration = new XsEnumeration();
                        _enumeration.setATT_Value(_value);
                        _restriction.append_Facet(_enumeration);
                    }
                    _simpleType.append_Restriction(_restriction);
                    schema.append_SimpleType(_simpleType);
                    break;
                case ELEMENTS:
                    XsGroup _group = new XsGroup();
                    _group.setATT_Name(entity._name);

                    AndOrTree _tree = entity._group;
                    andortree2group(_tree, _group);
                    schema.append_Group(_group);
                    break;
                case ATTRIBUTES:
                    XsAttributeGroup _attributeGroup = new XsAttributeGroup();
                    _attributeGroup.setATT_Name(entity._name);
                    // System.err.println("== SIZE" + entity._attributes.size());
                    for (Attribute _attribute : entity._attributes) {
//                        XsAttribute _attribute = new XsAttribute();
//                        _attribute.setATT_Name(_entityAttribute._name);
                        attribute2schemaNode(_attribute, _attributeGroup);
//                        _attributeGroup.append_Attribute(_attribute);
                    }

                    schema.append_AttributeGroup(_attributeGroup);
                    break;
            }
        }
    }

    private static void attribute2schemaNode(Attribute attribute,
            XsAttributeGroup attributeGroup) {

        XsAttribute _xsAttribute = new XsAttribute();
        _xsAttribute.setATT_Name(attribute._name);
        
        if(attribute._type == Attribute.Type.NMTOKEN){
            
        }
        
        if (attribute._defaultValue != null) {
            if (attribute._dvstatus == Attribute.DVStatus.FIXED) {
                _xsAttribute.setATT_Fixed(attribute._defaultValue);
            } else {
                _xsAttribute.setATT_Default(attribute._defaultValue);
            }
        }

        if (attribute._type == Attribute.Type.ENUM) {
            if (attribute._values != null) {
                XsSimpleType _simpleType = new XsSimpleType();
                XsRestriction _restriction = new XsRestriction();
                _restriction.setATT_Base(BASE_STRING);
                for (String _value : attribute._values) {
                    XsEnumeration _enumeration = new XsEnumeration();
                    _enumeration.setATT_Value(_value);
                    _restriction.append_Facet(_enumeration);
                }
                _simpleType.append_Restriction(_restriction);
                _xsAttribute.append_SimpleType(_simpleType);
            } else if (attribute._valuesFromEntity != null) {
                _xsAttribute.setATT_Type(attribute._valuesFromEntity);
            }
        }

        attributeGroup.append_Attribute(_xsAttribute);
    }

    private static void andortree2group(AndOrTree tree, JavaBean bean) {
        JavaBean groupType = null;
        if (tree._exp == AndOrTree.Type.AND) {
            groupType = new XsSequence();
        } else if (tree._exp == AndOrTree.Type.OR) {
            groupType = new XsChoice();

        }
        for (TreeNode _node : tree._nodes) {
            if (_node instanceof ChildNode) {
                ChildNode _child = (ChildNode) _node;
                XsElement _element = new XsElement();
                _element.setATT_Ref(_child._value);
                if (_child._unary == AndOrTree.Frequency.ZERO_OR_ONE) {
                    _element.setATT_MinOccurs("0");
                }
                groupType.appendChild(_element);
            } else if (_node instanceof AndOrTree) {
                andortree2group((AndOrTree) _node, groupType);
            }
        }

        bean.appendChild(groupType);

    }

    private static void element2schemaNode(Element element, XsSchema schema) {
        
    }
    private static String BASE_STRING = "xs:string";
    private static String BASE_NMTOKEN = "xs:NMTOKEN";
}