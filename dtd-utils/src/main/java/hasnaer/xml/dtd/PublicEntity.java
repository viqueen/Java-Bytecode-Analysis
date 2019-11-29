package hasnaer.xml.dtd;

/**
 *
 * @author hasnae rehioui
 */
public class PublicEntity extends Definition {

    protected String _meta;
    protected String _link;

    public PublicEntity(String rawName, String name, String meta, String link){
        super(name, rawName);
        _meta = meta;
        _link = link;
    }
}