package dk.itu.groupe.data;

import dk.itu.groupe.util.LinkedList;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class KDTree
{

    enum Dimension
    {

        X, Y
    };
    @SuppressWarnings("unchecked")
    private final Set<Edge> empty = (Set<Edge>) Collections.EMPTY_SET;
    KDTree HIGH, LOW;
    final Edge splitEdge;
    final double xmin, ymin, xmax, ymax;
    final Dimension dim;

    /**
     * Creates the KDTree-structure until there are no more elements.
     *
     * @param edges A list of edges that should be spread out on the tree.
     * @param xLeft The left x-coordinate.
     * @param yBottom The bottom y-coordinate.
     * @param xRight The right x-coordinate.
     * @param yTop The top y-coordinate.
     * @throws IndexOutOfBoundsException If the list of edges is empty.
     */
    public KDTree(LinkedList<Edge> edges, double xLeft, double yBottom, double xRight, double yTop)
    {
        xmin = xLeft;
        ymin = yBottom;
        xmax = xRight;
        ymax = yTop;

        int size = edges.size();

        LinkedList<Edge> low = new LinkedList<>(), high = new LinkedList<>();
        splitEdge = edges.get(size / 2);
        if (ymax - ymin < xmax - xmin) {                                        // Label 1
            // Delta x is gratest.
            dim = Dimension.X;
            // Put the right elements where it belongs.
            for (Edge edge : edges) {                                           // Label 2
                if (edge == splitEdge) {                                       // Label 3
                    continue;
                }
                if (edge.getCenterX() < splitEdge.getCenterX()) {              // Label 4
                    low.add(edge);
                } else {
                    high.add(edge);
                }
            }
        } else {
            // Delta y is the same size or greater.
            dim = Dimension.Y;
            // Put the right elements where it belongs.
            for (Edge edge : edges) {                                           // Label 5
                if (edge == splitEdge) {                                       // Label 6
                    continue;
                }
                if (edge.getCenterY() < splitEdge.getCenterY()) {              // Label 7
                    low.add(edge);
                } else {
                    high.add(edge);
                }
            }
        }
        assert (size == high.size() + 1 + low.size());
        double[] lowBounds = new double[4], highBounds = new double[4];
        if (dim == Dimension.X) {                                               // Label 8
            lowBounds[0] = xmin;
            lowBounds[1] = ymin;
            lowBounds[2] = splitEdge.getCenterX();
            lowBounds[3] = ymax;

            highBounds[0] = splitEdge.getCenterX();
            highBounds[1] = ymin;
            highBounds[2] = xmax;
            highBounds[3] = ymax;
        } else {
            lowBounds[0] = xmin;
            lowBounds[1] = ymin;
            lowBounds[2] = xmax;
            lowBounds[3] = splitEdge.getCenterY();

            highBounds[0] = xmin;
            highBounds[1] = splitEdge.getCenterY();
            highBounds[2] = xmax;
            highBounds[3] = ymax;
        }

        if (!low.isEmpty()) {                                                   // Label 9
            LOW = new KDTree(low, lowBounds[0], lowBounds[1], lowBounds[2], lowBounds[3]);
        }
        if (!high.isEmpty()) {                                                  // Label 10
            HIGH = new KDTree(high, highBounds[0], highBounds[1], highBounds[2], highBounds[3]);
        }
    }

    /**
     * Returns the nearest edge.
     *
     * @param x The x-coordinate to look near.
     * @param y The y-coordinate to look near.
     * @return The edge that are nearest to the coordinates.
     */
    public Edge getNearest(double x, double y)
    {
        int lookupRadius = 500;
        Point2D p = new Point2D.Double(x, y);
        Set<Edge> ns = getEdges(x - lookupRadius, y - lookupRadius, x + lookupRadius, y + lookupRadius);
        double dist = Double.MAX_VALUE;
        Edge nearest = null;
        for (Edge edge : ns) {                                                  // Label 11
            Point2D start = null;
            Point2D last = null;
            for (PathIterator pi = edge.getShape().getPathIterator(null); !pi.isDone(); pi.next()) {    // Label 12
                double[] coords = new double[6];
                int type = pi.currentSegment(coords);
                switch (type) {                                                 // Label 13
                    case PathIterator.SEG_MOVETO:
                        start = last = new Point2D.Double(coords[0], coords[1]);
                        break;
                    case PathIterator.SEG_LINETO:
                        Point2D.Double pd = new Point2D.Double(coords[0], coords[1]);
                        Line2D line = new Line2D.Double(last, pd);
                        last = pd;
                        double d = line.ptSegDist(p);
                        if (d < dist) {
                            dist = d;
                            nearest = edge;
                        }
                        break;
                    case PathIterator.SEG_CLOSE:
                        line = new Line2D.Double(last, start);
                        d = line.ptSegDist(p);
                        if (d < dist) {
                            dist = d;
                            nearest = edge;
                        }
                        break;
                }
            }
        }
        return nearest;
    }

    /**
     * Returns the edges with the four parameters as a bounding rectangle.
     *
     * It actually adds a little more to the distance.
     *
     * @param leftX The left x-coordinate
     * @param bottomY The bottom y-coordinate
     * @param rightX The right x-coordinate
     * @param topY The top y-coordinate.
     * @return A list of edgedata containing the edges that are inside the
     * rectangle.
     */
    public Set<Edge> getEdges(double leftX, double bottomY, double rightX, double topY)
    {
        int offset = 5000;
        if (dim == Dimension.X) {                                               // Label 14
            if (rightX + offset < xmin || leftX - offset > xmax) {              // Label 15
                return empty;
            }
        } else {
            if (topY + offset < ymin || bottomY - offset > ymax) {              // Label 16
                return empty;
            }
        }

        Set<Edge> edgeList;
        if (LOW != null) {                                                      // Label 17
            edgeList = LOW.getEdges(leftX, bottomY, rightX, topY);
            if (edgeList.isEmpty()) {                                           // Label 18
                edgeList = new HashSet<>();
            }
            if (HIGH != null) {                                                 // Label 19
                edgeList.addAll(HIGH.getEdges(leftX, bottomY, rightX, topY));
            }
        } else if (HIGH != null) {                                              // Label 20
            edgeList = HIGH.getEdges(leftX, bottomY, rightX, topY);
            if (edgeList.isEmpty()) {                                           // Label 21
                edgeList = new HashSet<>();
            }
        } else {
            edgeList = new HashSet<>();
        }
        if (splitEdge.getShape().intersects(leftX, bottomY, rightX - leftX, topY - bottomY)) { // Label 22
            edgeList.add(splitEdge);
        }
        return edgeList;
    }
}
