package hasnaer.ds.tree.andor;

import hasnaer.ds.tree.TreeNode;
import jregex.Matcher;
import jregex.Pattern;

/**
 *
 * @author hasnae rehioui
 */
public abstract class AndOrTreeParser<Value> {

    private Pattern _andOrPattern;
    private Matcher _frequencyMatcher;
    private Matcher _andOrMatcher;
    private Class _nodeClazz;

    public AndOrTreeParser(Pattern nodePattern,
            Pattern typePattern, Pattern frequencyPattern,
            Class nodeClazz) {
        _frequencyMatcher = new Pattern("({freq}" + frequencyPattern.toString() + ").*").matcher();
        _andOrMatcher = new Pattern("({and_or}" + typePattern.toString() + ").*").matcher();

        _andOrPattern = new Pattern("({node}" + nodePattern + ")({frequency}"
                + frequencyPattern + ")?" + "(({type}"
                + typePattern + ")({REST}.+))?");
        _nodeClazz = nodeClazz;

    }

    public AndOrTreeParser(Pattern nodePattern,
            Pattern typePattern, Pattern frequencyPattern) {

        _frequencyMatcher = new Pattern("({freq}" + frequencyPattern.toString() + ").*").matcher();
        _andOrMatcher = new Pattern("({and_or}" + typePattern.toString() + ").*").matcher();

        _andOrPattern = new Pattern("({node}" + nodePattern + ")({frequency}"
                + frequencyPattern + ")?" + "(({type}"
                + typePattern + ")({REST}.+))?");
    }

    public void parse(String stream, AndOrTree tree) throws Exception {
        stream = stream.replaceAll("\\s*", "");
        Matcher _matcher = _andOrPattern.matcher();

        if (stream != null && stream.length() > 0) {
            if (_matcher.matches(stream)) {
                String _node = _matcher.group("node");
                String _frequency = _matcher.group("frequency");
                String _type = _matcher.group("type");
                String _REST = _matcher.group("REST");

                tree._exp = str2type(_type);
                AndOrTreeNode _andOrNode = (AndOrTreeNode) _nodeClazz.newInstance();
                _andOrNode.init(_node);
                
                _andOrNode._unary = str2frequency(_frequency);
                tree._nodes.add(_andOrNode);

                nextAndOrToken(_REST, tree);
            } else if (stream.startsWith("(")) {
                int _closing = findMatchingParen(stream);
                AndOrTree _subTree = new AndOrTree();
                parse(stream.substring(1, _closing), _subTree);

                // try to consume frequency
                _closing = consumeFrequency(_closing, stream, _subTree);

                if (_closing < stream.length()) {
                    if (_andOrMatcher.matches(stream.substring(_closing))) {
                        String _and_or = _andOrMatcher.group("and_or");
                        AndOrTree.Type _treeType = str2type(_and_or);
                        if (tree._exp == null) {
                            tree._exp = _treeType;
                        } else if (tree._exp != _treeType) {
                            throw new Exception("PARSE ERROR: wrong and-or combination");
                        }
                        if (_subTree._unary == null) {
                            _subTree._unary = AndOrTree.Frequency.EXACTLY_ONE;
                        }
                        tree._nodes.add(_subTree);
                        nextAndOrToken(stream.substring(_closing + _and_or.length()), tree);
                    } else {
                        throw new Exception("PARSE ERROR: invalid syntax");
                    }
                } else if (tree._exp == null) {
                    // case first and only tree
                    tree._unary = _subTree._unary;
                    tree._exp = _subTree._exp;
                    for (TreeNode _node : _subTree._nodes) {
                        tree._nodes.add(_node);
                    }
                } else {
                    // case last tree
                    tree._nodes.add(_subTree);
                }
            }
        }
        if (tree._exp == null) {
            tree._exp = AndOrTree.Type.AND;
        }
        if (tree._unary == null) {
            tree._unary = AndOrTree.Frequency.EXACTLY_ONE;
        }
    }

    private int consumeFrequency(int closing, String stream,
            AndOrTree tree) {
        closing++;
        if (closing < stream.length()) {
            if (_frequencyMatcher.matches(stream.substring(closing))) {
                String _freq = _frequencyMatcher.group("freq");
                tree._unary = str2frequency(_freq);
                closing += _freq.length();
            }
        }
        return closing;
    }

    private int findMatchingParen(String stream) {
        int open = 1;
        int index = 0;
        while (open != 0) {
            index++;
            if (stream.charAt(index) == '(') {
                open++;
            } else if (stream.charAt(index) == ')') {
                open--;
            }
        }
        return index;
    }

    private void nextAndOrToken(String stream, AndOrTree tree)
            throws Exception {
        if (stream != null && stream.length() > 0) {
            Matcher _matcher = _andOrPattern.matcher(stream);
            if (_matcher.matches()) {

                String _node = _matcher.group("node");
                String _frequency = _matcher.group("frequency");
                String _type = _matcher.group("type");
                String _REST = _matcher.group("REST");
                 AndOrTreeNode _andOrNode = (AndOrTreeNode) _nodeClazz.newInstance();
                _andOrNode.init(_node);
                _andOrNode._unary = str2frequency(_frequency);
                tree._nodes.add(_andOrNode);
                AndOrTree.Type _treeType = str2type(_type);
                if (_treeType != null) {
                    if (_treeType == tree._exp) {
                        nextAndOrToken(_REST, tree);
                    } else {
                        throw new Exception("PARSE ERROR: wrong and-or combination");
                    }
                } else {
                    // end of stream
                }
            } else if (stream.startsWith("(")) {
                parse(stream, tree);
            }
        }
    }

    public abstract AndOrTree.Type str2type(String _str);

    public abstract AndOrTree.Frequency str2frequency(String _str);
}