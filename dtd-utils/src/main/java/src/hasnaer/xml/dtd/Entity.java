package hasnaer.xml.dtd;

import hasnaer.ds.tree.andor.AndOrTree;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hasnae rehioui
 */
public class Entity extends Definition {

    protected String _rootPackage;
    protected String _className;
    protected String _data;
    protected Type _type;
    protected AndOrTree _group;
    protected List<Attribute> _attributes;
    protected List<String> _values;
    protected boolean _processed;
    protected List<String> _interfaces;
    protected List<String> _childInterfaces;
    protected int _readyState = -1;
    protected int _parentCnt = 0;
    protected boolean _rendered;

    public Entity(String rawName, String name, String data) {
        super(rawName, name);
        _data = data;
        _interfaces = new ArrayList<String>(){
            @Override
            public boolean add(String data){
                if(!contains(data)){
                    return super.add(data);
                }
                return false;
            }
        };

        _childInterfaces = new ArrayList<String>(){
            @Override
            public boolean add(String data){
                if(!contains(data)){
                    return super.add(data);
                }
                return false;
            }
        };
    }


    public enum Type {

        VALUES, ELEMENTS, ATTRIBUTES,VALUE;
    }

    public boolean isReady() {
        if (++_readyState == _interfaces.size()) {
            return true;
        }
        return false;
    }

}