package dk.itu.groupe.pathfinding;

import dk.itu.groupe.data.Edge;
import dk.itu.groupe.pathfinding.Graph.WeightedEdge;
import dk.itu.groupe.data.Node;
import dk.itu.groupe.util.IndexedMinPQ;
import dk.itu.groupe.util.Stack;
import java.util.Arrays;

/**
 * This class calculates and represents the shortest path from a Node to another
 * in a Graph.
 *
 * It uses an Euclidian-distance as heuristics for the shortest-path-search. If
 * it is used for fastest-path search the heuristic returns 0, in other words it
 * is using Dijkstras algorithm until it finds the destination Node.
 *
 */
public class ShortestPath
{
    private final Graph g;
    private final double[] distTo;
    private final WeightedEdge[] edgeTo;
    private final IndexedMinPQ<Double> priorityQueue;
    private static Node[] nodeMap;
    private final boolean driveTime;

    /**
     * The constructor calculates the shortest path from a Node to another in
     * Graph g.
     *
     * It makes all the calculations on creation, so don't create instances of
     * this class unless you really need it.
     *
     * It uses A*-algorithm for shortest path search, and Dijkstra, that stops
     * when the destination has been found, for fastest path-search.
     *
     * @param g The Graph that contains the Nodes and Edges used to calculate
     * the shortest/fastest path.
     * @param from The index of the from-Node.
     * @param to The index of the destination-Node.
     * @param driveTime States wheter the path is calculated by distance or by
     * driveTime.
     * @param nodeMap The map of nodes. Used to make coordinate-lookups.
     */
    public ShortestPath(Graph g, int from, int to, boolean driveTime, Node[] nodeMap)
    {
        this.driveTime = driveTime;
        this.g = g;
        ShortestPath.nodeMap = nodeMap;
        distTo = new double[g.V()];
        edgeTo = new WeightedEdge[g.V()];
        Arrays.fill(distTo, Double.POSITIVE_INFINITY);
        distTo[from] = 0.0;

        // relax vertices in order of distance from s
        priorityQueue = new IndexedMinPQ<Double>(g.V());
        priorityQueue.insert(from, distTo[from]);
        while (!priorityQueue.isEmpty()) {
            int v = priorityQueue.delMin();
            // If we have found the shortest path to the destination, we are done.
            if (v == to) {
                return;
            }
            // Otherwise we continue to relax the edges.
            for (WeightedEdge e : g.adjacent(v)) {
                relax(e, to);
            }
        }
    }
    
    public boolean pathByDriveTime()
    {
        return driveTime;
    }

    /**
     * This method checks whether a new shortest path has been found.
     *
     * If a shorter path has been found, it will change the path to this new
     * shortest path to this Node.
     *
     * @param g The Graph that contains the Nodes and Edges used to calculate
     * the shortest/fastest path.
     * @param v The index of the current Node.
     * @param to The index of the destination-Node.
     */
    private void relax(WeightedEdge e, int t)
    {
        int v = e.from, w = e.to;
        if (distTo[w] > distTo[v] + e.getWeight(driveTime)) {
            distTo[w] = distTo[v] + e.getWeight(driveTime);
            edgeTo[w] = e;
            if (priorityQueue.contains(w)) {
                priorityQueue.decreaseKey(w, distTo[w] + heuristic(w, t));
            } else {
                priorityQueue.insert(w, distTo[w] + heuristic(w, t));
            }
        }
    }

    /**
     * Returns the distance from the source-Node to this Node.
     *
     * @param v The index of the Node.
     * @return The distance from the source-Node to this Node.
     */
    public double distTo(int v)
    {
        return distTo[v];
    }

    /**
     * States wheter or not there is a path between the source-Node and this
     * Node.
     *
     * @param v The id of this Node.
     * @return true if there is a path between the source-Node and this node.
     * false otherwise.
     */
    public boolean hasPathTo(int v)
    {
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns the path from the source-Node to this node.
     *
     * @param v The id of this Node.
     * @return The path from the source-Node to this node.
     */
    public Stack<Edge> pathTo(int v)
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

    /**
     * A simple and optimistic heuristic to make the algorithm faster.
     *
     * For shortest path: Euclidean-distance.
     * For fastest path: Euclidean-distance divided by 130 km/h.
     *
     * @param s The current Node.
     * @param t The destination Node.
     * @return The euclidian distance from Node v, to node to, if shortest path.
     * 0 otherwise.
     */
    private double heuristic(int s, int t)
    {
        if (driveTime) {
            return Math.sqrt(Math.pow(nodeMap[s].x() - nodeMap[t].x(), 2) + Math.pow(nodeMap[s].y() - nodeMap[t].y(), 2)) / 1000 / 130 * 60;
        } else {
            return Math.sqrt(Math.pow(nodeMap[s].x() - nodeMap[t].x(), 2) + Math.pow(nodeMap[s].y() - nodeMap[t].y(), 2));
        }
    }
}
