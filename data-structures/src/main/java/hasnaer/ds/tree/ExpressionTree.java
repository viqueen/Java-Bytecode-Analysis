package hasnaer.ds.tree;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hasnae rehioui
 */
public abstract class ExpressionTree extends TreeNode {

    public BinaryExp _exp;
    public List<TreeNode> _nodes;

    public ExpressionTree(){
        _nodes = new ArrayList<TreeNode>();
    }

    public void setExp(BinaryExp exp){
        _exp = exp;
    }
    
    public static interface BinaryExp{
    }
}