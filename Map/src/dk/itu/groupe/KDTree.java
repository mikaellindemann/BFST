package dk.itu.groupe;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class KDTree {

    enum Dimension {

        X, Y
    };

    private final Set<EdgeData> empty = new HashSet<>();
    private KDTree HIGH, LOW;
    private Set<EdgeData> edges;
    private EdgeData centerEdge;
    private double xmin, ymin, xmax, ymax;
    private Dimension dim;

    public KDTree(Set<EdgeData> edges) {
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

        if (edges.size() <= 100) {
            this.edges = edges;
            //centerEdge = edges.toArray(new EdgeData[0])[edges.size() / 2];
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
            if (!low.isEmpty()) {
                LOW = new KDTree(low);
            }
            if (!high.isEmpty()) {
                HIGH = new KDTree(high);
            }
        }
    }

    public EdgeData nearest(double x, double y) {
        if (HIGH == null && LOW == null && edges != null) {
            EdgeData edge = null;
            double distX = Double.MAX_VALUE;
            double distY = Double.MAX_VALUE;
            for (EdgeData e : edges) {
                if (edge == null) {
                    edge = e;
                }
                NodeData n = Map.nodeMap.get(e.FNODE);
                if (Math.abs(n.X_COORD - x) < distX &&
                        Math.abs(n.Y_COORD - y) < distY) {
                    edge = e;
                    distX = Math.abs(n.X_COORD - x);
                    distY = Math.abs(n.Y_COORD - y);
                }
            }
            assert(edge != null);
            return edge;
        }
        if (dim == Dimension.X) {
            if (x == Map.nodeMap.get(centerEdge.FNODE).X_COORD) {
                return centerEdge;
            } else if (x < Map.nodeMap.get(centerEdge.FNODE).X_COORD) {
                if (LOW == null) {
                    if (HIGH == null) {
                        return centerEdge;
                    }
                    return HIGH.nearest(x, y);
                }
                return LOW.nearest(x, y);
            } else {
                if (HIGH == null) {
                    if (LOW == null) {
                        return centerEdge;
                    }
                    return LOW.nearest(x, y);
                }
                return HIGH.nearest(x, y);
            }
        } else {
            if (y == Map.nodeMap.get(centerEdge.FNODE).Y_COORD) {
                return centerEdge;
            } else if (y < Map.nodeMap.get(centerEdge.FNODE).Y_COORD) {
                if (LOW == null) {
                    if (HIGH == null) {
                        return centerEdge;
                    }
                    return HIGH.nearest(x, y);
                }
                return LOW.nearest(x, y);
            } else {
                if (HIGH == null) {
                    if (LOW == null) {
                        return centerEdge;
                    }
                    return LOW.nearest(x, y);
                }
                return HIGH.nearest(x, y);
            }
        }
    }

    public Set<EdgeData> getEdges(double xLow, double yLow, double xHigh, double yHigh) {
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
            Set<EdgeData> edgeSet = new HashSet<>();
            if (LOW != null) {
                edgeSet.addAll(LOW.getEdges(xLow, yLow, xHigh, yHigh));

                if (centerEdge != null) {
                    edgeSet.add(centerEdge);

                    if (HIGH != null) {
                        edgeSet.addAll(HIGH.getEdges(xLow, yLow, xHigh, yHigh));
                    }
                }
            } else if (HIGH != null) {
                if (centerEdge != null) {
                    edgeSet.add(centerEdge);
                    edgeSet.addAll(HIGH.getEdges(xLow, yLow, xHigh, yHigh));
                }
                else {
                    edgeSet.add(centerEdge);
                }
            }
            return edgeSet;
        }
    }
}
