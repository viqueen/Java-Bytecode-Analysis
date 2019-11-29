package hasnaer.ds.tree.andor;

import hasnaer.ds.tree.TreeNode;

/**
 *
 * @author hasnae rehioui
 */
public abstract class AndOrTreeNode<Value> extends TreeNode {
    public Value _value;
    public abstract void init(Value value);
    
    public AndOrTreeNode(){
        
    }
    public AndOrTreeNode(String data){
        _value = (Value) data;
    }    
    public AndOrTreeNode(Value value){
        super();
        _value = value;
    }
    public AndOrTreeNode(Value value, UnaryExp unary){
        this(value);
        _unary = unary;
    }
    @Override
    public String toString(){
        return _value.toString();
    }
}