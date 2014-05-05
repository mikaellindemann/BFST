/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.groupe;

import dk.itu.groupe.EdgeWeightedDigraph.WeightedEdge;
import java.util.Stack;

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

        // check optimality conditions
        assert check(G, s);
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
    public Iterable<Edge> pathTo(int v)
    {
        if (!hasPathTo(v)) {
            return null;
        }
        Stack<Edge> path = new Stack<>();
        for (WeightedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from]) {
            path.push(e.e);
        }
        return path;
    }

    // check optimality conditions:
    // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
    // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
    private boolean check(EdgeWeightedDigraph G, int s)
    {

        // check that edge weights are nonnegative
        for (Edge e : G.edges()) {
            if (e.getLength() < 0) {
                System.err.println("negative edge weight detected");
                return false;
            }
        }

        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < G.V(); v++) {
            if (v == s) {
                continue;
            }
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < G.V(); v++) {
            for (WeightedEdge e : G.adj(v)) {
                int w = e.to;
                if (distTo[v] + e.getWeight(driveTime) < distTo[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < G.V(); w++) {
            if (edgeTo[w] == null) {
                continue;
            }
            WeightedEdge e = edgeTo[w];
            int v = e.from;
            if (w != e.to) {
                return false;
            }
            if (distTo[v] + e.getWeight(driveTime) != distTo[w]) {
                System.err.println("edge " + e + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }
}
