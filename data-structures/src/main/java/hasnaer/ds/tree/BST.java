package hasnaer.ds.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author hasnae rehioui
 */
public class BST<Key extends Comparable<Key>, Value extends Serializable> {

    protected Node<Key, Value> _root;

    public void insert(Key key, Value value) {
        _root = insert(_root, key, value);
    }

    private Node<Key, Value> insert(Node<Key, Value> node, Key key, Value value) {
        if (node == null) {
            node = new Node<Key, Value>(key, value);
        } else {
            int _cmp = node._key.compareTo(key);
            if (_cmp > 0) {
                node._left = insert(node._left, key, value);
            } else if (_cmp < 0) {
                node._right = insert(node._right, key, value);
            } else {
                node._value = value;
            }
        }
        return node;
    }

    public List<Node<Key, Value>> traverse(TraversalType type,
            TraversalImp impl)
            throws Exception {
        List<Node<Key, Value>> _traversal = new ArrayList<Node<Key, Value>>();
        this.getClass().getDeclaredMethod(type.toString() + "_" + impl.toString(),
                Node.class, List.class).invoke(this, _root, _traversal);
        return _traversal;
    }

    private void INORDER_LNR_RECURSIVE(Node<Key, Value> root,
            List<Node<Key, Value>> traversal) {
        if (root != null) {
            INORDER_LNR_RECURSIVE(root._left, traversal);
            traversal.add(root);
            INORDER_LNR_RECURSIVE(root._right, traversal);
        }
    }

    private void INORDER_LNR_ITERATIVE(Node<Key, Value> root,
            List<Node<Key, Value>> traversal) {

        boolean over = false;
        Stack<Node<Key, Value>> _stack = new Stack<Node<Key, Value>>();
        Node<Key, Value> _current = _root;
        while (!over) {
            if (_current != null) {
                _stack.push(_current);
                _current = _current._left;
            } else {
                if (_stack.isEmpty()) {
                    over = true;
                } else {
                    _current = _stack.pop();
                    traversal.add(_current);
                    _current = _current._right;
                }
            }
        }
    }

    private void INORDER_LNR_MORRIS(Node<Key, Value> root,
            List<Node<Key, Value>> traversal) {

        Node _current = _root;
        Node _temp = null;

        while (_current != null) {
            if (_current._left == null) {
                traversal.add(_current);
                _current = _current._right;
            } else {
                _temp = _current._left;
                while (_temp._right != null && _temp._right != _current) {
                    _temp = _temp._right;
                }
                if (_temp._right == null) {
                    _temp._right = _current;
                    _current = _current._left;
                } else {
                    traversal.add(_current);
                    _temp._right = null;
                    _current = _current._right;
                }
            }
        }
    }

    private void PREORDER_NLR_RECURSIVE(Node<Key, Value> root,
            List<Node<Key, Value>> traversal) {
        if (root != null) {
            traversal.add(root);
            PREORDER_NLR_RECURSIVE(root._left, traversal);
            PREORDER_NLR_RECURSIVE(root._right, traversal);
        }
    }

    private void PREORDER_NLR_ITERATIVE(Node<Key, Value> root,
            List<Node<Key, Value>> traversal) {
        Stack<Node<Key, Value>> _stack = new Stack<Node<Key, Value>>();
        _stack.push(_root);
        while (!_stack.isEmpty()) {
            Node<Key, Value> _node = _stack.pop();
            traversal.add(_node);
            if (_node._right != null) {
                _stack.push(_node._right);
            }
            if (_node._left != null) {
                _stack.push(_node._left);
            }
        }
    }

    private void PREORDER_NLR_MORRIS(Node<Key, Value> root,
            List<Node<Key, Value>> traversal) {
        Node _current = _root;
        Node _temp = null;

        while (_current != null) {
            if (_current._left == null) {
                traversal.add(_current);
                _current = _current._right;
            } else {
                _temp = _current._left;
                while (_temp._right != null && _temp._right != _current) {
                    _temp = _temp._right;
                }
                if (_temp._right == null) {
                    traversal.add(_current);
                    _temp._right = _current;
                    _current = _current._left;
                } else {
                    _temp._right = null;
                    _current = _current._right;
                }
            }
        }
    }

    private void POSTORDER_LRN_RECURSIVE(Node<Key, Value> node,
            List<Node<Key, Value>> traversal) {
        if (node != null) {
            POSTORDER_LRN_RECURSIVE(node._left, traversal);
            POSTORDER_LRN_RECURSIVE(node._right, traversal);
            traversal.add(node);
        }
    }

    private void POSTORDER_LRN_ITERATIVE(Node<Key, Value> node,
            List<Node<Key, Value>> traversal) {

        Node<Key, Value> _current;
        Stack<Node<Key, Value>> _stack = new Stack<Node<Key, Value>>();
        _stack.push(node);
        while (!_stack.isEmpty()) {
            _current = _stack.pop();
            traversal.add(0, _current);
            if (_current._left != null) {
                _stack.push(_current._left);
            }
            if (_current._right != null) {
                _stack.push(_current._right);
            }
        }
    }

    private void POSTORDER_LRN_MORRIS(Node<Key, Value> node,
            List<Node<Key, Value>> traversal) {
                Node _current = _root;
        Node _temp = null;

        while (_current != null) {
            if (_current._right == null) {
                traversal.add(0, _current);
                _current = _current._left;
            } else {
                _temp = _current._right;
                while (_temp._left != null && _temp._left != _current) {
                    _temp = _temp._left;
                }
                if (_temp._left == null) {
                    traversal.add(0, _current);
                    _temp._left = _current;
                    _current = _current._right;
                } else {
                    _temp._left = null;
                    _current = _current._left;
                }
            }
        }
    }

    public static class Node<Key extends Comparable<Key>,
            Value extends Serializable> {

        Key _key;
        Value _value;
        Node<Key, Value> _left;
        Node<Key, Value> _right;

        public Node(Key key, Value value) {
            _key = key;
            _value = value;
        }
        @Override
        public String toString() {
            return String.format("(%s:{%s})", _key, _value);
        }
    }

    public enum TraversalType {

        INORDER_LNR, PREORDER_NLR, POSTORDER_LRN;
    }

    public enum TraversalImp {

        RECURSIVE, ITERATIVE, MORRIS;
    }

    public static void main(String[] args) throws Exception {

        BST<Integer, Integer> _bst = new BST<Integer, Integer>();
        _bst.insert(5, 5);
        _bst.insert(8, 8);
        _bst.insert(4, 4);
        _bst.insert(7, 7);
        _bst.insert(2, 2);
        _bst.insert(3, 3);
        _bst.insert(10, 10);
        _bst.insert(9, 9);

        System.err.println(_bst.traverse(TraversalType.POSTORDER_LRN,
                BST.TraversalImp.MORRIS));
        System.err.println(_bst.traverse(TraversalType.POSTORDER_LRN,
                BST.TraversalImp.RECURSIVE));
        System.err.println(_bst.traverse(TraversalType.POSTORDER_LRN,
                BST.TraversalImp.ITERATIVE));        
    }
}