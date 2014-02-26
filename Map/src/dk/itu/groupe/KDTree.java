package dk.itu.groupe;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author pecto
 */
public class KDTree
{

    enum Dimension
    {

        X, Y
    };

    private final Set<EdgeData> empty = new HashSet<>();
    private KDTree HIGH, LOW;
    private Set<EdgeData> edges;
    private EdgeData centerEdge;
    private double xmin, ymin, xmax, ymax;
    private Dimension dim;

    public KDTree(Set<EdgeData> edges)
    {
        xmin = ymin = Double.MAX_VALUE;
        xmax = ymax = Double.MIN_VALUE;
        for (EdgeData edge : edges) {
            NodeData firstNode = Map.nodeMap.get(edge.FNODE);
            NodeData lastNode = Map.nodeMap.get(edge.TNODE);

            // Set xmin, xmax, ymin and ymax.
            for (NodeData nd : new NodeData[]{firstNode, lastNode}) {
                if (nd.X_COORD < xmin) {
                    xmin = nd.X_COORD;
                } else if (nd.X_COORD > xmax) {
                    xmax = nd.X_COORD;
                }
                if (nd.Y_COORD < ymin) {
                    ymin = nd.Y_COORD;
                } else if (nd.Y_COORD > ymax) {
                    ymax = nd.Y_COORD;
                }
            }
        }

        if (edges.size() <= 1000) {
            this.edges = edges;
        } else {
            Set<EdgeData> low = new HashSet<>(), high = new HashSet<>();
            centerEdge = (EdgeData) edges.toArray()[edges.size() / 2];
            edges.remove(centerEdge);
            if (ymax - ymin < xmax - xmin) {
                dim = Dimension.X;
                // Put the right elements where it belongs.
                for (EdgeData edge : edges) {
                    if (Map.nodeMap.get(edge.FNODE).X_COORD < Map.nodeMap.get(centerEdge.FNODE).X_COORD) {
                        low.add(edge);
                    } else {
                        high.add(edge);
                    }
                }
            } else {
                dim = Dimension.Y;
                // Put the right elements where it belongs.
                for (EdgeData edge : edges) {
                    if (Map.nodeMap.get(edge.FNODE).Y_COORD < Map.nodeMap.get(centerEdge.FNODE).Y_COORD) {
                        low.add(edge);
                    } else {
                        high.add(edge);
                    }
                }
            }
            LOW = new KDTree(low);
            HIGH = new KDTree(high);
        }
    }

    public Set<EdgeData> getEdges(double xLow, double yLow, double xHigh, double yHigh)
    {
        if (dim == Dimension.X) {
            if (xHigh < xmin || xLow > xmax) {
                return empty;
            }
        } else {
            if (yHigh < ymin || yLow > ymax) {
                return empty;
            }
        }

        if (centerEdge == null) {
            return edges;
        } else {
            Set<EdgeData> edgeSet = LOW.getEdges(xLow, yLow, xHigh, yHigh);
            edgeSet.add(centerEdge);

            edgeSet.addAll(HIGH.getEdges(xLow, yLow, xHigh, yHigh));
            return edgeSet;
        }
    }
}
