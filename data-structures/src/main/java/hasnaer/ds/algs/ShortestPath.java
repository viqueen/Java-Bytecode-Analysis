package hasnaer.ds.algs;

import hasnaer.ds.graph.Graph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

/**
 *
 * @author hasnae rehioui
 */
public class ShortestPath {

    public static class Dijkstra<Key extends Comparable<Key>, Value> {

        Graph<Key, Value> _graph;

        public Dijkstra(Graph<Key, Value> graph) {
            _graph = graph;
        }

        public List<Graph.Node<Key, Value>> execute(Key from, Key to) {
            List<Graph.Node<Key, Value>> _path = new ArrayList<Graph.Node<Key, Value>>();

            // init
            Map<Key, Node<Key>> _data = new HashMap<Key, Node<Key>>();
            for (Entry<Key, Graph.Node<Key, Value>> entry : _graph._nodes.entrySet()) {
                _data.put(entry.getKey(), new Node<Key>(entry.getKey(), null, Double.MAX_VALUE));
            }

            _data.get(from).setData(null, 0.0, false);

            Key _min = getMinNode(_data);
            while (true) {
                if (_min == null) {
                    return null;
                }
                if (_min.compareTo(to) == 0) {
                    break;
                }
                _data.get(_min).setVisited(true);
                Queue<Graph.Edge<Key>> _edges = _graph._nodes.get(_min)._edges;
                for (Graph.Edge<Key> _edge : _edges) {
                    Double _dist = _data.get(_min)._cost + _edge._cost;
                    if (_dist < _data.get(_edge._to)._cost && !_data.get(_edge._to)._visited) {
                        _data.get(_edge._to).setData(_min, _dist, false);
                    }
                }
                _min = getMinNode(_data);
            }
            Node<Key> _node = _data.get(to);
            while (_node != null) {
                _path.add(0, _graph._nodes.get(_node._node));
                _node = _data.get(_node._parent);
            }
            return _path;
        }

        private Key getMinNode(Map<Key, Node<Key>> data) {
            Key _min = null;
            Double _minValue = Double.MAX_VALUE;
            for (Entry<Key, Node<Key>> entry : data.entrySet()) {
                if (!entry.getValue()._visited) {
                    if (entry.getValue()._cost < _minValue) {
                        _minValue = entry.getValue()._cost;
                        _min = entry.getKey();
                    }
                }
            }
            return _min;
        }

        public static class Node<Key extends Comparable<Key>>    {

            public Key _node;
            public Key _parent;
            public Double _cost;
            public boolean _visited;

            public Node(Key node, Key parent, Double cost) {
                _node = node;
                _parent = parent;
                _cost = cost;
                _visited = false;
            }

            public void setData(Key parent, Double cost, boolean visited) {
                _parent = parent;
                _cost = cost;
                _visited = visited;
            }

            public void setVisited(boolean visited) {
                _visited = visited;
            }
        }
    }
}
