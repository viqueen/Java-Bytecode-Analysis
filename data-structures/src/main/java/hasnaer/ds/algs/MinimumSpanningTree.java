package hasnaer.ds.algs;

import hasnaer.ds.graph.Graph;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author hasnae rehioui
 */
public class MinimumSpanningTree {

    public static class Prim<Key extends Comparable<Key>, Value> {

        public List<Graph.Edge<Key>> execute(Graph<Key, Value> graph) {
            List<Graph.Edge<Key>> _mst = new ArrayList<Graph.Edge<Key>>();
            Key _start = graph._nodes.keySet().iterator().next();
            PriorityQueue<Graph.Edge<Key>> _queue = new PriorityQueue<Graph.Edge<Key>>();

            _queue.addAll(graph._nodes.get(_start)._edges);
            Set<Key> _u = new HashSet<Key>();

            _u.add(_start);
            while (_mst.size() < graph._nodes.size() - 1) {
                Graph.Edge<Key> _edge = _queue.remove();
                while (_u.contains(_edge._to)) {
                    _edge = _queue.remove();
                }
                _mst.add(_edge);
                _u.add(_edge._to);
                _queue.addAll(graph._nodes.get(_edge._to)._edges);
            }

            return _mst;
        }
    }

    public static class Kruskal {
    }
}