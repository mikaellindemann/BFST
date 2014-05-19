package dk.itu.groupe.pathfinding;

import dk.itu.groupe.data.Edge;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents an EdgeWeighted Digraph.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) &amp;
 * Mikael Jepsen (mlin@itu.dk) with great inspiration from Algorithms 4th
 * Edition, Wayne &amp; Sedgewick.
 */
public class Graph
{

    private final int numberOfNodes;
    private int numberOfEdges;
    private Set<WeightedEdge>[] adjacencyLists;

    /**
     * Creates a new Graph with <code>nodes</code> Nodes.
     *
     * @param numberOfNodes The number of nodes in the Graph.
     */
    @SuppressWarnings("unchecked")
    public Graph(int numberOfNodes)
    {
        if (numberOfNodes < 0) {
            throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
        }
        this.numberOfNodes = numberOfNodes;
        this.numberOfEdges = 0;
        adjacencyLists = (Set<WeightedEdge>[]) new Set[numberOfNodes];
    }

    /**
     * Returns the number of Nodes in the graph.
     *
     * @return The number of Nodes in the graph.
     */
    public int V()
    {
        return numberOfNodes;
    }

    /**
     * Returns the number of Edges in the graph.
     *
     * @return The number of Edges in the graph.
     */
    public int E()
    {
        return numberOfEdges;
    }

    /**
     * Adds an Edge to the Graph.
     *
     * @param e The edge to add.
     * @throws ArrayIndexOutOfBoundsException If the Edge contains Node-ids that
     * is greater than the graphs number of Ids.
     */
    public void addEdge(Edge e)
    {
        switch (e.getOneWay()) {
            case NO:
                addE(new WeightedEdge(e, e.from().id(), e.to().id()));
                addE(new WeightedEdge(e, e.to().id(), e.from().id()));
                break;
            case FROM_TO:
                addE(new WeightedEdge(e, e.from().id(), e.to().id()));
                break;
            case TO_FROM:
                addE(new WeightedEdge(e, e.to().id(), e.from().id()));
                break;
        }
    }

    private void addE(WeightedEdge e)
    {
        if (adjacencyLists[e.from] == null) {
            adjacencyLists[e.from] = new HashSet<>();
        }
        adjacencyLists[e.from].add(e);
        numberOfEdges++;
    }

    /**
     * Returns a view of all Edges that goes from <code>node</code>
     *
     * @param node The id of the Node.
     * @return A view of all Edges that goes from <code>node</code>.
     */
    Iterable<WeightedEdge> adjacent(int node)
    {
        if (node < 0 || node >= numberOfNodes) {
            throw new IndexOutOfBoundsException("vertex " + node + " is not between 0 and " + (numberOfNodes - 1));
        }
        if (adjacencyLists[node] == null) {
            adjacencyLists[node] = new HashSet<>();
        }
        return adjacencyLists[node];
    }

    /**
     * Returns a view of all Edges in the Graph.
     *
     * @return A view of all Edges in the Graph.
     */
    public Iterable<Edge> edges()
    {
        Set<Edge> set = new HashSet<>();
        for (int v = 0; v < numberOfNodes; v++) {
            for (WeightedEdge e : adjacent(v)) {
                set.add(e.e);
            }
        }
        return set;
    }

    class WeightedEdge
    {

        Edge e;
        int from;
        int to;

        WeightedEdge(Edge e, int from, int to)
        {
            this.e = e;
            this.from = from;
            this.to = to;
        }

        double getWeight(boolean driveTime)
        {
            return driveTime ? e.getDriveTime() : e.getLength();
        }
    }
}
