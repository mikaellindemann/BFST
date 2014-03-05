package dk.itu.groupe;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class KDTree {

    enum Dimension {

        X, Y
    };

    private final List<EdgeData> empty = new LinkedList<>();
    private KDTree HIGH, LOW;
    private final EdgeData centerEdge;
    private final double xmin, ymin, xmax, ymax;
    private final Dimension dim;

    public KDTree(List<EdgeData> edges, double xMin, double yMin, double xMax, double yMax) {
        xmin = xMin;
        ymin = yMin;
        xmax = xMax;
        ymax = yMax;

        int size = edges.size();

        List<EdgeData> low = new LinkedList<>(), high = new LinkedList<>();
        centerEdge = (EdgeData) edges.toArray()[edges.size() / 2];
        edges.remove(centerEdge);
        if (ymax - ymin > xmax - xmin) {
            dim = Dimension.X;
            // Put the right elements where it belongs.
            while (!edges.isEmpty()) {
                EdgeData edge = edges.remove(0);
                if (Map.nodeMap.get(edge.FNODE).X_COORD < Map.nodeMap.get(centerEdge.FNODE).X_COORD) {
                    low.add(edge);
                } else {
                    high.add(edge);
                }
            }
        } else {
            dim = Dimension.Y;
            // Put the right elements where it belongs.
            while (!edges.isEmpty()) {
                EdgeData edge = edges.remove(0);
                if (Map.nodeMap.get(edge.FNODE).Y_COORD < Map.nodeMap.get(centerEdge.FNODE).Y_COORD) {
                    low.add(edge);
                } else {
                    high.add(edge);
                }
            }
        }
        assert (edges.isEmpty());
        assert (size == high.size() + 1 + low.size());
        NodeData c = Map.nodeMap.get(centerEdge.FNODE);
        double[] lowBounds = new double[4], highBounds = new double[4];
        if (dim == Dimension.X) {
            lowBounds[0] = xmin;
            lowBounds[1] = ymin;
            lowBounds[2] = centerEdge.line.getX1();
            lowBounds[3] = ymax;
            
            highBounds[0] = centerEdge.line.getX1();
            highBounds[1] = ymin;
            highBounds[2] = xmax;
            highBounds[3] = ymax;
        } else {
            lowBounds[0] = xmin;
            lowBounds[1] = ymin;
            lowBounds[2] = xmax;
            lowBounds[3] = centerEdge.line.getY1();
            
            highBounds[0] = xmin;
            highBounds[1] = centerEdge.line.getY1();
            highBounds[2] = xmax;
            highBounds[3] = ymax;
        }
        
        if (!low.isEmpty()) {
            LOW = new KDTree(low, lowBounds[0], lowBounds[1], lowBounds[2], lowBounds[3]);
        }
        if (!high.isEmpty()) {
            HIGH = new KDTree(high, highBounds[0], highBounds[1], highBounds[2], highBounds[3]);
        }

    }

    public EdgeData nearest(double x, double y) {
        if (HIGH == null && LOW == null) {
            return centerEdge;
        }
        if (dim == Dimension.X) {
            if (x == Map.nodeMap.get(centerEdge.FNODE).X_COORD) {
                return centerEdge;
            } else if (x < Map.nodeMap.get(centerEdge.FNODE).X_COORD) {
                if (LOW == null) {
                    return HIGH.nearest(x, y);
                }
                return LOW.nearest(x, y);
            } else {
                if (HIGH == null) {
                    return LOW.nearest(x, y);
                }
                return HIGH.nearest(x, y);
            }
        } else {
            if (y == Map.nodeMap.get(centerEdge.FNODE).Y_COORD) {
                return centerEdge;
            } else if (y < Map.nodeMap.get(centerEdge.FNODE).Y_COORD) {
                if (LOW == null) {
                    return centerEdge;
                }
                return LOW.nearest(x, y);
            } else {
                if (HIGH == null) {
                    return centerEdge;
                }
                return HIGH.nearest(x, y);
            }
        }
    }

    public List<EdgeData> getEdges(double xLow, double yLow, double xHigh, double yHigh) {
        //Fix this code!
        if (dim == Dimension.X) {
            if (yHigh + Map.getFactor() * 1000 < ymin || yLow - Map.getFactor() * 1000 > ymax) {
                return empty;
            }
        } else {
            if (xHigh + Map.getFactor() * 1000 < xmin || xLow - Map.getFactor() * 1000 > xmax) {
                return empty;
            }
        }

        List<EdgeData> edgeList = new LinkedList<>();
        if (LOW != null) {
            edgeList.addAll(LOW.getEdges(xLow, yLow, xHigh, yHigh));

            if (centerEdge != null) {
                edgeList.add(centerEdge);

                if (HIGH != null) {
                    edgeList.addAll(HIGH.getEdges(xLow, yLow, xHigh, yHigh));
                }
            }
        } else if (HIGH != null) {
            if (centerEdge != null) {
                edgeList.add(centerEdge);
                edgeList.addAll(HIGH.getEdges(xLow, yLow, xHigh, yHigh));
            }
        } else {
            edgeList.add(centerEdge);
        }
        return edgeList;
    }
}
