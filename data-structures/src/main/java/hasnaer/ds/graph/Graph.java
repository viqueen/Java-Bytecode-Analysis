package hasnaer.ds.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 *
 * @author hasnae rehioui
 */
public class Graph<Key extends Comparable<Key>, Value> {

    public Map<Key, Node<Key, Value>> _nodes;
    protected boolean _isDirected;

    private TraversalType _traversalType;
    
    public Graph() {
        this(false);
    }

    public Graph(boolean isDirected) {
        _isDirected = isDirected;
        _nodes = new HashMap<Key, Node<Key, Value>>();
    }

    public void insertNode(Key key, Value value) {
        _nodes.put(key, new Node<Key, Value>(key, value));
    }

    public void addEdge(Key from, Key to) {
        addEdge(from, to, 0.0);
    }

    public void addEdge(Key from, Key to, Double cost) {
        _nodes.get(from)._edges.add(new Edge<Key>(from, to, cost));
        if (!_isDirected) {
            _nodes.get(to)._edges.add(new Edge<Key>(to, from, cost));
        }
    }
      
    
    public List<Node<Key, Value>> traverse(Key start, TraversalType type){
        List<Node<Key, Value>> _traversal = new ArrayList<Node<Key, Value>>();
        _traversalType = type;
        
        LinkedList<Key> _list = new LinkedList<Key>(){
            @Override
            public boolean add(Key e) {
                if(_traversalType == TraversalType.BFS){
                    addLast(e);
                } else {
                    addFirst(e);
                }
                return true;
            }
            
        };
        
        _list.add(start);
        while(!_list.isEmpty()){
            Node<Key, Value> _node = _nodes.get(_list.removeFirst());
            if(!_traversal.contains(_node)){
                _traversal.add(_node);
                for(Edge<Key> _edge : _node._edges){
                    _list.add(_edge._to);
                }
            }
        }
        
        return _traversal;
    }
            
    public static class Node<Key extends Comparable<Key>, Value>
            implements Comparable<Node<Key, Value>> {

        Key _key;
        Value _value;
        public PriorityQueue<Edge<Key>> _edges;

        public Node(Key key, Value value) {
            _key = key;
            _value = value;
            _edges = new PriorityQueue<Edge<Key>>();
        }

        @Override
        public int compareTo(Node<Key, Value> o) {
            return _key.compareTo(o._key);
        }
        
        @Override
        public String toString(){
            return _key.toString();
        }
    }

    public static class Edge<Key extends Comparable<Key>>
            implements Comparable<Edge<Key>> {

        public Key _from;
        public Key _to;
        public Double _cost;

        public Edge(Key from, Key to, Double cost) {
            _from = from;
            _to = to;
            _cost = cost;
        }

        @Override
        public int compareTo(Edge<Key> o) {
            return _cost.compareTo(o._cost);
        }
    }

    public enum TraversalType{
        DFS, BFS;
    }
    
    public static void main(String[] args) {
        Graph<Character, Character> _graph = new Graph<Character, Character>();

        _graph.insertNode('A', 'A');
        _graph.insertNode('B', 'B');
        _graph.insertNode('C', 'C');
        _graph.insertNode('D', 'D');
        _graph.insertNode('E', 'E');
        _graph.insertNode('F', 'F');
        _graph.insertNode('G', 'G');
        _graph.insertNode('H', 'H');

        _graph.addEdge('A', 'B');
        _graph.addEdge('A', 'C');
        _graph.addEdge('B', 'C');
        _graph.addEdge('B', 'D');
        _graph.addEdge('C', 'F');
        _graph.addEdge('D', 'E');
        _graph.addEdge('E', 'F');
        _graph.addEdge('E', 'G');
        _graph.addEdge('F', 'G');
        _graph.addEdge('F', 'H');
        
        System.err.println(_graph.traverse('A', TraversalType.BFS));
    }
}