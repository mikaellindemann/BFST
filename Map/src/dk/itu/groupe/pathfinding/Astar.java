/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.groupe.pathfinding;

import dk.itu.groupe.data.Edge;
import dk.itu.groupe.pathfinding.EdgeWeightedDigraph.WeightedEdge;
import dk.itu.groupe.data.Node;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The <tt>DijkstraSP</tt> class represents a data type for solving the
 * single-source shortest paths problem in edge-weighted digraphs where the edge
 * weights are nonnegative.
 * <p>
 * This implementation uses Dijkstra's algorithm with a binary heap. The
 * constructor takes time proportional to <em>E</em> log <em>V</em>, where
 * <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 * Afterwards, the <tt>distTo()</tt> and <tt>hasPathTo()</tt> methods take
 * constant time and the <tt>pathTo()</tt> method takes time proportional to the
 * number of edges in the shortest path returned.
 * <p>
 * For additional documentation, see <a href="/algs4/44sp">Section 4.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class Astar
{

    private final double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private final WeightedEdge[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
    private final IndexMinPQ<Double> pq;    // priority queue of vertices
    private static Node[] nodeMap;
    private final boolean driveTime;

    private double heuristic(int s, int t)
    {
        if (driveTime) {
            return 0;
        } else {
            return Math.sqrt(Math.pow(nodeMap[s].x() - nodeMap[t].x(), 2) + Math.pow(nodeMap[s].y() - nodeMap[t].y(), 2));
        }
    }

    /**
     * Computes a shortest paths tree from <tt>s</tt> to every other vertex in
     * the edge-weighted digraph <tt>G</tt>.
     *
     * @param G the edge-weighted digraph
     * @param s the source vertex
     * @param t the destination vertex
     * @param driveTime
     * @param nodeMap
     * @throws IllegalArgumentException if an edge weight is negative
     * @throws IllegalArgumentException unless 0 &le; <tt>s</tt> &le; <tt>V</tt>
     * - 1
     */
    public Astar(EdgeWeightedDigraph G, int s, int t, boolean driveTime, Node[] nodeMap)
    {
        this.driveTime = driveTime;
        Astar.nodeMap = nodeMap;
        distTo = new double[G.V()];
        edgeTo = new WeightedEdge[G.V()];
        for (int v = 0; v < G.V(); v++) {
            distTo[v] = Double.POSITIVE_INFINITY;
        }
        distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(G.V());
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            if (v == t) {
                return;
            }
            for (WeightedEdge e : G.adj(v)) {
                relax(e, t);
            }
        }
    }

    // relax edge e and update pq if changed
    private void relax(WeightedEdge e, int t)
    {
        int v = e.from, w = e.to;
        if (distTo[w] > distTo[v] + e.getWeight(driveTime)) {
            distTo[w] = distTo[v] + e.getWeight(driveTime);
            edgeTo[w] = e;
            if (pq.contains(w)) {
                pq.decreaseKey(w, distTo[w] + heuristic(w, t));
            } else {
                pq.insert(w, distTo[w] + heuristic(w, t));
            }
        }
    }

    /**
     * Returns the length of a shortest path from the source vertex <tt>s</tt>
     * to vertex <tt>v</tt>.
     *
     * @param v the destination vertex
     * @return the length of a shortest path from the source vertex <tt>s</tt>
     * to vertex <tt>v</tt>;
     * <tt>Double.POSITIVE_INFINITY</tt> if no such path
     */
    public double distTo(int v)
    {
        return distTo[v];
    }

    /**
     * Is there a path from the source vertex <tt>s</tt> to vertex <tt>v</tt>?
     *
     * @param v the destination vertex
     * @return <tt>true</tt> if there is a path from the source vertex
     * <tt>s</tt> to vertex <tt>v</tt>, and <tt>false</tt> otherwise
     */
    public boolean hasPathTo(int v)
    {
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path from the source vertex <tt>s</tt> to vertex
     * <tt>v</tt>.
     *
     * @param v the destination vertex
     * @return a shortest path from the source vertex <tt>s</tt> to vertex
     * <tt>v</tt>
     * as an iterable of edges, and <tt>null</tt> if no such path
     */
    public Deque<Edge> pathTo(int v)
    {
        if (!hasPathTo(v)) {
            return null;
        }
        Deque<Edge> path = new ArrayDeque<>();
        for (WeightedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from]) {
            path.push(e.e);
        }
        return path;
    }
}
