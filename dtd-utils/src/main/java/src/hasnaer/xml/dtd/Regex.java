package hasnaer.xml.dtd;

import hasnaer.ds.tree.andor.AndOrTree.Frequency;
import hasnaer.ds.tree.andor.AndOrTree.Type;
import hasnaer.ds.tree.andor.AndOrTreeParser;
import jregex.Pattern;

/**
 *
 * @author hasnae rehioui
 */
public class Regex {

    public final static Pattern _dtdNode = new Pattern("!(({_nodeType}ELEMENT"
            + "|ATTLIST|ENTITY)|({_comment}\\-\\-))\\s*({_nodeDef}.*)");

    final static Pattern _child = new Pattern("\\s*#({data}(P)?CDATA)"
            + "|({element}[a-zA-Z0-9\\.\\-_]+)"
            + "|((&|%)({entity}[a-zA-Z0-9\\.\\-_]+);)\\s*");

    final static Pattern _andOr = new Pattern(",|\\|");

    final static Pattern _frequency = new Pattern("\\+|\\*|\\?");

    final static Pattern _elDef = new Pattern("((%|&)({fromEntity}[a-zA-Z0-9\\.:\\-_]+);|"
            + "(({category}EMPTY|ANY)|({childNodes}\\(\\s*.+\\s*\\)("
            + _frequency.toString() + ")?)))\\s*");

    final static Pattern _element =
            new Pattern("({name}[a-zA-Z0-9\\.\\-_%;]+)\\s+" + _elDef.toString());

    final static Pattern _entity = new Pattern("%\\s+({name}[a-zA-Z0-9\\.:\\-_]+)\\s+"
            + "(PUBLIC\\s+(('|\")({public}.*)('|\")\\s+))?('|\")({data}.*)('|\")\\s*");

//    final static Pattern _attDefault = new Pattern("#(REQUIRED|IMPLIED)" +
//            "|#FIXED\\s*('|\")?({fixed}\\s*[:\\.\\-/a-zA-Z0-9]+\\s*)('|\")?" +
//            "|('|\")?({value}\\s*[:\\.\\-/a-zA-Z0-9]+\\s*)('|\")?");
//
    final static Pattern _attDefault = new Pattern("#(REQUIRED|IMPLIED)" +
            "|#FIXED\\s*('|\")({fixed}[^'^\"]*)('|\")\\s*" +
            "|('|\")({value}[^'^\"]*)('|\")\\s*");

    final static Pattern _attType = new Pattern("CDATA"
            + "|ID(REF(S)?)?"
            + "|NMTOKEN(S)?"
            + "|ENTIT(Y|IES)"
            + "|NOTATION"
            + "|(&|%)({typeFromEntity}[a-zA-Z0-9\\.:\\-_]+);"
            + "|\\(\\s*({enumValues}[a-zA-Z0-9\\-:\\./]+(\\s*\\|\\s*[a-zA-Z0-9\\-\\./]+)*)\\s*\\)");

//    final static Pattern _attDef = new Pattern("(((%|&)({fromEntity}[a-zA-Z0-9\\.:\\-_]+);)|"
//            + "(({attName}[a-zA-Z0-9:\\.\\-_%;]+)\\s+"
//            + "({attType}" + _attType.toString() + ")\\s+"
//            + "({attDefault}" + _attDefault.toString() + ")))\\s*({REST}.+)?");

    final static Pattern _attDef = new Pattern(""
            + "(({attName}[a-zA-Z0-9:\\.\\-_%;]+)\\s+("
            + "({attType}" + _attType.toString() + ")\\s+"
            + "({attDefault}" + _attDefault.toString() + "))?)\\s*({REST}.+)?");

    final static Pattern _attlist = new Pattern("({_elName}[a-zA-Z0-9\\.\\-_%;]+)\\s+({_attDef}.+)");

    public final static Pattern _value = new Pattern(".*%({entityName}.*);.*");

    final static Pattern _publicEntity = new Pattern("(%|&)({entityName}[a-zA-Z0-9\\.:\\-_]+);({REST}.*)");

    public final static AndOrTreeParser _andOrTreeParser = new AndOrTreeParser(_child, _andOr,
            _frequency, ChildNode.class) {

        @Override
        public Type str2type(String _str) {
            if ("|".equals(_str)) {
                return Type.OR;
            } else if (",".equals(_str)) {
                return Type.AND;
            }
            return null;
        }

        @Override
        public Frequency str2frequency(String _str) {
            if ("?".equals(_str)) {
                return Frequency.ZERO_OR_ONE;
            } else if ("*".equals(_str)) {
                return Frequency.ZERO_OR_MORE;
            } else if ("+".equals(_str)) {
                return Frequency.AT_LEAST_ONE;
            }
            return Frequency.EXACTLY_ONE;
        }
    };

}