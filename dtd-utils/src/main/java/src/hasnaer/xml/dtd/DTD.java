package hasnaer.xml.dtd;

import hasnaer.ds.tree.TreeNode;
import hasnaer.ds.tree.andor.AndOrTree;
import hasnaer.data.util.IOUtils;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import jregex.Matcher;

/**
 *
 * @author hasnae rehioui
 */
public class DTD {

    public String _rootPackage;
    protected String _elementPrefix;
    public Map<String, Element> _elements;
    public Map<String, PublicEntity> _publicEntities;
    public Map<String, Entity> _entities;
    public Map<String, List<Attribute>> _attributesByElements;
    protected boolean _isBound;

    public DTD(InputStream dtdStream, String elementPrefix) throws Exception {
        _elements = new HashMap<String, Element>();
        _entities = new HashMap<String, Entity>();
        _publicEntities = new HashMap<String, PublicEntity>();
        _attributesByElements = new HashMap<String, List<Attribute>>();
        _elementPrefix = elementPrefix;
        parse(IOUtils.stream2string(dtdStream).replaceAll("\r|\n", " ").trim());
        bind();
    }

    public DTD(String rootPackage, InputStream stream, List<DTD> dependencies)
            throws Exception {
        this(rootPackage, stream, dependencies, "");
    }

    public DTD(String dtdStream, String elementPrefix) throws Exception {
        _elements = new HashMap<String, Element>();
        _entities = new HashMap<String, Entity>();
        _publicEntities = new HashMap<String, PublicEntity>();
        _attributesByElements = new HashMap<String, List<Attribute>>();
        _elementPrefix = elementPrefix;
        parse(dtdStream.replaceAll("\r|\n", " ").trim());
        bind();
    }
    
    public DTD(String rootPackage, InputStream stream, List<DTD> dependencies,
            String elementPrefix)
            throws Exception {
        _rootPackage = rootPackage;
        _elements = new HashMap<String, Element>();
        _entities = new HashMap<String, Entity>();
        _publicEntities = new HashMap<String, PublicEntity>();
        _elementPrefix = elementPrefix;
        if (dependencies != null) {
            for (DTD dtd : dependencies) {
                _elements.putAll(dtd._elements);
                _entities.putAll(dtd._entities);
                _publicEntities.putAll(dtd._publicEntities);
            }
        }
        parse(IOUtils.stream2string(stream).replaceAll("\r|\n", " ").trim());
        bind();
    }

    private void bind() throws Exception {
        if (!_isBound) {
            // System.err.println("****** START DTD BINDING");

            // System.err.println("-- Bind ELEMENTS");
            for (Entry<String, Element> _entry : _elements.entrySet()) {
                Element _element = _entry.getValue();
                List<String> _names = new ArrayList<String>();
                bindGroup(_element, _element._childNodes, _names);
            }

            // System.err.println("-- Bind ENTITIES");
            for (Entry<String, Entity> _entry : _entities.entrySet()) {
                Entity _entity = _entry.getValue();
                // System.err.println("** bind : " + _entity._name);
                if (_entity._type == Entity.Type.ELEMENTS) {
                    // System.err.println("has kids");
                    bindEntityGroup(_entity._name, _entity._group, new ArrayList<String>());
                } else {
                    // System.err.println("none");
                }
            }

            // System.err.println("****** STOP DTD BINDING");
            _isBound = true;
        }
    }

    private void bindEntityGroup(String entityName, AndOrTree group, List<String> names) {
        List<TreeNode> _nodes = group._nodes;
        for (TreeNode _node : _nodes) {
            if (_node instanceof ChildNode) {
                ChildNode _child = (ChildNode) _node;
                if (!names.contains(_child._value)) {
                    if (_child._type == ChildNode.Type.ELEMENT) {
                        names.add(_child._value);
                        _elements.get(_child._value)._interfaces.add(entityName);
                    } else if (_child._type == ChildNode.Type.ENTITY) {
                        names.add(_child._value);
                        _entities.get(_child._value)._interfaces.add(entityName);
                        _entities.get(entityName)._childInterfaces.add(_child._value);
                    }
                }
            } else if (_node instanceof AndOrTree) {
                bindEntityGroup(entityName, (AndOrTree) _node, names);
            }
        }
    }

    private void bindGroup(Element parent, AndOrTree group, List<String> names) throws Exception {
        List<TreeNode> _nodes = group._nodes;
        if (_nodes != null) {
            for (TreeNode _node : _nodes) {
                if (_node instanceof ChildNode) {
                    ChildNode _child = (ChildNode) _node;
                    if (_child._type == ChildNode.Type.ELEMENT) {
                        if (!names.contains(_child._value) && !_elements.get(_child._value)._rendered) {
                            _elements.get(_child._value)._parents.add(parent);
                            parent._childCnt++;
                        }
                    } else if (_child._type == ChildNode.Type.ENTITY) {
                        entityAsElements(_child._value);
                    }
                } else if (_node instanceof AndOrTree) {
                    bindGroup(parent, (AndOrTree) _node, names);
                }
            }
        }
    }

    private void parse(String dtdStream) throws Exception {
        
        // System.err.println(dtdStream);
        
        Matcher _matcher = Regex._dtdNode.matcher();
        Matcher _publicEntityMatcher = Regex._publicEntity.matcher();
        while (dtdStream != null && dtdStream.length() > 0) {
            int _start = dtdStream.indexOf("<");
            int _end = dtdStream.indexOf(">");

            if (_start == 0 && _end > 0) {
                String _node = dtdStream.substring(1, _end);
                if (_matcher.matches(_node)) {
                    String _nodeType = _matcher.group("_nodeType");
                    String _comment = _matcher.group("_comment");
                    if (_nodeType != null) {
                        DTD.class.getDeclaredMethod("do" + _nodeType,
                                String.class).invoke(this, _matcher.group("_nodeDef"));
                    } else if (_comment != null) {
                        _end = dtdStream.indexOf("-->") + 2;
                    }

                    dtdStream = dtdStream.substring(_end + 1).trim();
                } else {
                    throw new Exception("PARSE ERROR: Invalid DTD Node: " + _node);
                }
            } else if (_publicEntityMatcher.matches(dtdStream)) {
                String _publicEntityName = _publicEntityMatcher.group("entityName");
                PublicEntity _publicEntity = _publicEntities.get(_publicEntityName);
                // add dependency
                DTD _dependency = new DTD(_rootPackage + "."
                        + BeanGenUtil.makeJavaName(_publicEntity._name), new FileInputStream(_publicEntity._link),
                        Arrays.asList(new DTD[]{this}), _elementPrefix);

                _elements.putAll(_dependency._elements);
                _entities.putAll(_dependency._entities);
                _publicEntities.putAll(_dependency._publicEntities);

                dtdStream = _publicEntityMatcher.group("REST").trim();
            } else {
                throw new Exception("PARSE ERROR: Invalid DTD Syntax " + dtdStream.substring(0, 40));
            }


        }
    }

    public void doELEMENT(String elementDef) throws Exception {
        Matcher _matcher = Regex._element.matcher();
        Matcher _valueMatcher = Regex._value.matcher();
        if (_matcher.matches(elementDef)) {
//            // System.err.println(elementDef);
            String _rawName = _matcher.group("name");
            String _name = renderValue(_matcher.group("name"));

            String _fromEntity = _matcher.group("fromEntity");
            String _category = _matcher.group("category");
            String _childNodes = _matcher.group("childNodes");

            Element _element = null;
            if (_fromEntity != null) {
                entityAsElements(_fromEntity);
                _element = new Element(_rawName, _name, _elementPrefix,
                        _category, _entities.get(_fromEntity)._group);
            } else {
                AndOrTree _treeNodes = new AndOrTree();
                if (_childNodes != null) {
                    Regex._andOrTreeParser.parse(_childNodes, _treeNodes);
                }
                _element = new Element(_rawName, _name,
                        _elementPrefix, _category, _treeNodes);
                propagateEntityAsElements(_treeNodes);
            }
            _element._rootPackage = _rootPackage;

            if (_valueMatcher.matches(_rawName)) {
//                // System.err.println(_name + " is interface");
                _element._interfaces.add(_valueMatcher.group("entityName"));
            }

            _elements.put(_name, _element);

        } else {
            throw new Exception("PARSE ERROR: @element " + elementDef);
        }
    }

    public void doATTLIST(String attlistDef) throws Exception {
        Matcher _matcher = Regex._attlist.matcher();
        if (_matcher.matches(attlistDef)) {
//            // System.err.println(attlistDef);

            String _elName = renderValue(_matcher.group("_elName"));
            String _attDef = _matcher.group("_attDef");

            List<Attribute> _atts = extractAttributes(_attDef);
//            _attributesByElements.put(_elName, _atts);
            _elements.get(_elName)._attributes.addAll(_atts);

        } else {
            throw new Exception("PARSE ERROR: @attlist " + attlistDef);
        }
    }

    private List<Attribute> extractAttributes(String attDef) throws Exception {

//        // System.err.println("extractAttributes: " + attDef);
        Matcher _matcher = Regex._attDef.matcher();
        Matcher _entityNameMatcher = Regex._publicEntity.matcher();

        List<Attribute> _atts = new ArrayList<Attribute>();
        String _rest = attDef.trim();

        while (_rest != null && _matcher.matches(_rest)) {
            String _attName = _matcher.group("attName");
            String _attType = _matcher.group("attType");
            String _attDefault = _matcher.group("attDefault");

//            // System.err.println("REST: " + _rest);
//            // System.err.println("attName " + _attName);
//            // System.err.println("attType " + _attType);
//            // System.err.println("attDefault " + _attDefault);
//            String _fromEntity = _matcher.group("fromEntity");
            if (_attType == null && _attDefault == null) {
//                // System.err.println("entityAsAttributes: " + _attName);
                _entityNameMatcher.matches(_attName);
                _attName = _entityNameMatcher.group("entityName");
//                // System.err.println("entityAsAttributes: " + _attName);
                entityAsAttributes(_attName);
                _atts.addAll(_entities.get(_attName)._attributes);
            } else {
//                String _attName = _matcher.group("attName");

                String _typeFromEntity = _matcher.group("typeFromEntity");
                String _enumValues = _matcher.group("enumValues");
                String _fixed = _matcher.group("fixed");
                String _value = _matcher.group("value");

                Attribute _attribute = new Attribute(_attName,
                        renderValue(_attName));
                if (_typeFromEntity != null) {
                    entityAsAttributeType(_typeFromEntity);
                    Entity _entity = _entities.get(_typeFromEntity);
                    if (_entity._values != null) {
                        _attribute._type = Attribute.Type.ENUM;
                        _attribute._valuesFromEntity = _typeFromEntity;
                    } else {
                        _attribute._type = Attribute.Type.valueOf(_entity._data);
                    }
                } else if (_enumValues != null) {
                    _attribute._type = Attribute.Type.ENUM;
                    _attribute._values = new ArrayList<String>();
                    _attribute._values.addAll(extractEnumValues(_enumValues));
                } else if (_attType != null) {
                    _attribute._type = Attribute.Type.valueOf(_attType);
                }

                if (_fixed != null) {
                    _attribute._dvstatus = Attribute.DVStatus.FIXED;
                    _attribute._defaultValue = renderValue(_fixed);
                } else if (_value != null) {
                    _attribute._dvstatus = Attribute.DVStatus.OTHER;
                    _attribute._defaultValue = _value;
                } else if (_attDefault != null) {
                    _attribute._dvstatus = Attribute.DVStatus.valueOf(_attDefault.substring(1));
                }

                _atts.add(_attribute);
            }

            _rest = _matcher.group("REST");
            if (_rest != null) {
                _rest = _rest.trim();
            }

            // System.err.println("REST: " + _rest);


        }

        // System.err.println("atts: " + _atts);
        return _atts;
    }

    public void doENTITY(String entityDef) throws Exception {
        Matcher _matcher = Regex._entity.matcher();
//        // System.err.println("** entity:  " + entityDef);
        if (_matcher.matches(entityDef)) {
            String _rawName = _matcher.group("name");
            String _name = renderValue(_matcher.group("name"));
            String _data = _matcher.group("data");
            String _public = _matcher.group("public");
            if (_public != null) {
                PublicEntity _publicEntity = new PublicEntity(_rawName, _name,
                        _public, _data);
                _publicEntities.put(_name, _publicEntity);
            } else {
                Entity _entity = new Entity(_rawName, _name, _data);
                _entity._rootPackage = _rootPackage;
                _entities.put(_name, _entity);
            }
        } else {
            throw new Exception("PARSE ERROR: @entity " + entityDef);
        }
    }

    private void entityAsElements(String _fromEntity) throws Exception {
        Entity _entity = _entities.get(_fromEntity);
        if (!_entity._processed) {
            _entity._group = new AndOrTree();
            _entity._type = Entity.Type.ELEMENTS;
            Regex._andOrTreeParser.parse(_entity._data, _entity._group);
            propagateEntityAsElements(_entity._group);
            _entity._processed = true;
        }

    }

    private void entityAsAttributes(String fromEntity) throws Exception {
        // System.err.println("============entityAsAttributes: " + fromEntity);
        Entity _entity = _entities.get(fromEntity);
        if (!_entity._processed) {
            // System.err.println("not processed");
            _entity._type = Entity.Type.ATTRIBUTES;
            _entity._attributes = new ArrayList<Attribute>();
            _entity._attributes.addAll(extractAttributes(_entity._data));
            // System.err.println(_entity._attributes);
            _entity._processed = true;
        }
    }

    private void entityAsAttributeType(String _typeFromEntity) {
        Entity _entity = _entities.get(_typeFromEntity);
        if (!_entity._processed) {
            Matcher _matcher = Regex._attType.matcher();
            if (_matcher.matches(_entity._data)) {
                String _enumValues = _matcher.group("enumValues");
                if (_enumValues != null) {
                    _entity._type = Entity.Type.VALUES;
                    _entity._values = new ArrayList<String>();
                    _entity._values.addAll(extractEnumValues(_enumValues));
                }
            }
            _entity._processed = true;
        }
    }

    private List<String> extractEnumValues(String enumValues) {
        List<String> _values = new ArrayList<String>();
        String[] _tokens = enumValues.split("\\s*\\|\\s*");
        for (int i = 0; i < _tokens.length; i++) {
            _values.add(_tokens[i].trim());
        }
        return _values;
    }

    private String renderValue(String value) {
        Matcher _matcher = Regex._value.matcher();

        while (_matcher.matches(value)) {
            String _entityName = _matcher.group("entityName");
            value = value.replaceAll("%" + _entityName + ";",
                    renderValue(_entities.get(_entityName)._data));
            _entities.get(_entityName)._type = Entity.Type.VALUE;
            _entities.get(_entityName)._processed = true;
        }

        return value;
    }

    private void propagateEntityAsElements(AndOrTree _treeNodes)
            throws Exception {
//        // System.err.println("propagateEntityAsElements");
        List<TreeNode> _nodes = _treeNodes._nodes;
        for (TreeNode _node : _nodes) {
            if (_node instanceof AndOrTree) {
                propagateEntityAsElements((AndOrTree) _node);
            } else if (_node instanceof ChildNode) {
                ChildNode _child = (ChildNode) _node;
                if (_child._type.isEntity()) {
//                    // System.err.println(_child._value);
                    entityAsElements(_child._value);
                }
            }
        }
    }
}
