package hasnaer.xml.dtd;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author hasnae rehioui
 */
public class JavaBean {

    public Map<String, String> _attributes;
    public List _childNodes;
    public Type _type;
    public String _name;

    public JavaBean(Type type, String name) {
        this._type = type;
        this._name = name;
        this._attributes = new LinkedHashMap<String, String>();
        this._childNodes = new ArrayList();
    }

    public void setAttribute(String name, Object value) {
        this._attributes.put(name, value.toString());
    }

    public void appendChild(Object child) {
        this._childNodes.add(child);
    }

    public String renderXMLstring() {
        StringBuilder _builder = new StringBuilder();
        _builder.append("<");
        _builder.append(_name);

        Set<Entry<String, String>> _entries = _attributes.entrySet();
        for (Entry<String, String> _entry : _entries) {
            _builder.append(" ");
            _builder.append(_entry.getKey());
            _builder.append("=\"");
            _builder.append(_entry.getValue());
            _builder.append("\"");
        }
        if (this._type.equals(Type.EMPTY)) {
            _builder.append(" />");
        } else {
            _builder.append(">");
            for (Object _child : _childNodes) {
                if (_child instanceof JavaBean) {
                    _builder.append(((JavaBean) _child).renderXMLstring());
                } else {
                    _builder.append(_child.toString());
                }
            }
            _builder.append("</");
            _builder.append(_name);
            _builder.append(">");
        }
        return _builder.toString();
    }

    public enum Type {
        EMPTY, NONEMPTY;
    }
}