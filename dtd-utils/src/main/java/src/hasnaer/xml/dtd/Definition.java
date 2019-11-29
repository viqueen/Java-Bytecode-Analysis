package hasnaer.xml.dtd;

/**
 *
 * @author hasnae rehioui
 */
public abstract class Definition {
    protected String _name;
    protected String _rawName;
    public Definition(String name, String rawName){
        _name = name;
        _rawName = rawName;
    }
}