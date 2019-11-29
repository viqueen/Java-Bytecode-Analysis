package hasnaer.xml.dtd;

import hasnaer.ds.tree.andor.AndOrTreeNode;
import jregex.Matcher;

/**
 *
 * @author hasnae rehioui
 */
public class ChildNode extends AndOrTreeNode<String> {

    protected Type _type;

    public ChildNode() {
        super();
    }

    @Override
    public void init(String value) {
        Matcher _matcher = Regex._child.matcher();
        if (_matcher.matches(value)) {
            String _data = _matcher.group("data");
            String _element = _matcher.group("element");
            String _entity = _matcher.group("entity");
            if (_data != null) {
                _value = _data;
                _type = Type.valueOf(_data);
            } else if (_element != null) {
                _value = _element;
                _type = Type.ELEMENT;
            } else if (_entity != null) {
                _value = _entity;
                _type = Type.ENTITY;
            }
        }
    }

    @Override
    public String toString() {
        return _value.toString();
    }

    public enum Type {

        ELEMENT, PCDATA, CDATA, ENTITY;

        boolean isElement() {
            return this.equals(ELEMENT);
        }

        boolean isData(){
            return this.equals(PCDATA) || this.equals(CDATA);
        }

        boolean isEntity(){
            return this.equals(ENTITY);
        }
    }
}