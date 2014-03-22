package dk.itu.groupe;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class KDTree
{

    enum Dimension
    {

        X, Y
    };

    private final List<Node> empty = new LinkedList<>();
    private KDTree HIGH, LOW;
    private final Node centerNode;
    private final double xmin, ymin, xmax, ymax;
    private final Dimension dim;

    /**
     * Creates the KDTree-structure until there are no more elements.
     *
     * @param nodes A list of edges that should be spread out on the tree.
     * @param xLeft The left x-coordinate.
     * @param yBottom The bottom y-coordinate.
     * @param xRight The right x-coordinate.
     * @param yTop The top y-coordinate.
     */
    public KDTree(List<Node> nodes, double xLeft, double yBottom, double xRight, double yTop)
    {
        xmin = xLeft;
        ymin = yBottom;
        xmax = xRight;
        ymax = yTop;

        int size = nodes.size();

        List<Node> low = new LinkedList<>(), high = new LinkedList<>();
        centerNode = nodes.remove(nodes.size() / 2);
        if (ymax - ymin < xmax - xmin) {
            // Delta x is gratest.
            dim = Dimension.X;
            // Put the right elements where it belongs.
            while (!nodes.isEmpty()) {
                Node node = nodes.remove(0);
                if (node.X_COORD < centerNode.X_COORD) {
                    low.add(node);
                } else {
                    high.add(node);
                }
            }
        } else {
            // Delta y is the same size or greater.
            dim = Dimension.Y;
            // Put the right elements where it belongs.
            while (!nodes.isEmpty()) {
                Node node = nodes.remove(0);
                if (node.Y_COORD < centerNode.Y_COORD) {
                    low.add(node);
                } else {
                    high.add(node);
                }
            }
        }
        assert (nodes.isEmpty());
        assert (size == high.size() + 1 + low.size());
        double[] lowBounds = new double[4], highBounds = new double[4];
        if (dim == Dimension.X) {
            lowBounds[0] = xmin;
            lowBounds[1] = ymin;
            lowBounds[2] = centerNode.X_COORD;
            lowBounds[3] = ymax;

            highBounds[0] = centerNode.X_COORD;
            highBounds[1] = ymin;
            highBounds[2] = xmax;
            highBounds[3] = ymax;
        } else {
            lowBounds[0] = xmin;
            lowBounds[1] = ymin;
            lowBounds[2] = xmax;
            lowBounds[3] = centerNode.Y_COORD;

            highBounds[0] = xmin;
            highBounds[1] = centerNode.Y_COORD;
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

    /**
     * Returns the nearest edge.
     *
     * @param x The x-coordinate to look near.
     * @param y The y-coordinate to look near.
     * @return The edge that are nearest to the coordinates.
     */
    public Edge getNearest(double x, double y)
    {
        List<Node> ns = getNodes(x, y, x, y);
        double dist = Double.MAX_VALUE;
        Edge nearest = null;
        for (Node node : ns) {
            for (Edge edge : node.getEdges()) {
                double d = edge.line.ptSegDist(x, y);
                if (d < dist) {
                    dist = d;
                    nearest = edge;
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
    public List<Node> getNodes(double leftX, double bottomY, double rightX, double topY)
    {
        if (dim == Dimension.X) {
            if (rightX + Math.abs((leftX - rightX) / 10) < xmin || leftX - Math.abs(leftX - rightX) / 10 > xmax) {
                return empty;
            }
        } else {
            if (topY + Math.abs(topY - bottomY) / 10 < ymin || bottomY - Math.abs(topY - bottomY) / 10 > ymax) {
                return empty;
            }
        }

        List<Node> nodeList = new LinkedList<>();
        if (LOW != null) {
            nodeList.addAll(LOW.getNodes(leftX, bottomY, rightX, topY));

            if (centerNode != null) {
                nodeList.add(centerNode);

                if (HIGH != null) {
                    nodeList.addAll(HIGH.getNodes(leftX, bottomY, rightX, topY));
                }
            }
        } else if (HIGH != null) {
            if (centerNode != null) {
                nodeList.add(centerNode);
                nodeList.addAll(HIGH.getNodes(leftX, bottomY, rightX, topY));
            }
        } else {
            nodeList.add(centerNode);
        }
        return nodeList;
    }
}
