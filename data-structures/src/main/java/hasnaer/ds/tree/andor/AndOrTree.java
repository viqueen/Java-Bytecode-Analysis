package hasnaer.ds.tree.andor;

import hasnaer.ds.tree.ExpressionTree;
import hasnaer.ds.tree.TreeNode;

/**
 *
 * @author hasnae rehioui
 */
public class AndOrTree extends ExpressionTree {

    public AndOrTree(){
        super();
    }
    
    public enum Type implements ExpressionTree.BinaryExp{
        AND, OR;
        public static Type select(Object or){
            return (or != null)?OR:AND;
        }
    }

    public enum Frequency implements TreeNode.UnaryExp{
        EXACTLY_ONE,
        AT_LEAST_ONE,
        ZERO_OR_ONE,
        ZERO_OR_MORE;        
    }

    @Override
    public String toString(){
        return String.format("[\n_type: %s\n_freq: %s\n_nodes:%s\n]", _exp, _unary, _nodes);
    }
}
