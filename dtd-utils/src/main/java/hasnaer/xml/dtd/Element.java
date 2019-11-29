package hasnaer.xml.dtd;

import hasnaer.ds.tree.andor.AndOrTree;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hasnae rehioui
 */
public class Element extends Definition {

    
    protected String _prefix;
    protected String _rootPackage;
    protected String _className;
    protected Category _category;
    protected AndOrTree _childNodes;
    protected List<Element> _parents;
    protected int _childCnt;
    protected List<String> _interfaces;
    protected int _readyState = -1;
    protected List<Attribute> _attributes;
    protected boolean _rendered;

    public Element(String rawName, String name, String prefix, String category, AndOrTree childNodes) {
        super(name, rawName);
        _prefix = prefix;
        _category = Category.fromStr(category);
        _childNodes = childNodes;
        _parents = new ArrayList<Element>();
        _childCnt = 0;
        _interfaces = new ArrayList<String>(){
            @Override
            public boolean add(String data){
                if(!contains(data)){
                    return super.add(data);
                }
                return false;
            }
        };
        _attributes = new ArrayList<Attribute>();
    }

    public boolean isReady() {
        if (++_readyState == _childCnt) {
            return true;
        }
        return false;
    }

    public Element(String rawName, String name) {
        super(name, rawName);
    }

    public enum Category {

        ANY, EMPTY, CHILDNODES;

        public static Category fromStr(String category) {
            if (category == null) {
                return CHILDNODES;
            } else {
                return valueOf(category);
            }
        }

        public JavaBean.Type toBeanType() {
            return this.equals(EMPTY) ? JavaBean.Type.EMPTY : JavaBean.Type.NONEMPTY;
        }
    }

    @Override
    public String toString() {
        return String.format("{\n_name:%s\n_category:%s\n_childNodes:%s\n_attributes:%s\n}",
                _name, _category, _childNodes, _attributes);
    }
}