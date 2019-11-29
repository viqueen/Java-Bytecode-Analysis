package hasnaer.xml.dtd;

import java.util.List;

/**
 *
 * @author hasnae rehioui
 */
public class Attribute extends Definition {

    protected String _data;
    protected Type _type;
    protected DVStatus _dvstatus;
    protected String _valuesFromEntity;
    protected List<String> _values;
    protected String _defaultValue;

    public Attribute(String rawName, String name) {
        super(name, rawName);
    }

    public enum Type {
        CDATA, ENUM,
        ID, IDREF, IDREFS,
        NMTOKEN, NMTOKENS,
        ENTITY, ENTITIES,
        NOTATION;
    }

    public enum DVStatus {
        REQUIRED, IMPLIED, FIXED, OTHER;
    }

    @Override
    public String toString(){
        return String.format("_n:%s|_t:%s|_v:%s ", _name, _type, _values);
    }
}