package dk.itu.groupe;

import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import javax.swing.JOptionPane;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class Model extends Observable
{

    // These are the lowest and highest coordinates in the dataset.
    // If we change dataset, these are likely to change.
    private final static double lowestX_COORD = 442254.35659;
    private final static double highestX_COORD = 892658.21706;
    private final static double lowestY_COORD = 6049914.43018;
    private final static double highestY_COORD = 6402050.98297;

    // Bounds of the window.
    private double leftX, bottomY, rightX, topY;
    private double factor;
    private double ratioX;
    private double ratioY;

    private String roadname;

    private Point pressed, dragged;
    private MouseTool mouseTool;

    private int width, height;

    private final KDTree nodes;

    public Model()
    {
        SplashLoader.updateSplash(0);
        String dir = "./res/data/";
        mouseTool = MouseTool.ZOOM;
        
        final HashMap<Integer, Node> nodeMap = new HashMap<>();
        KrakLoader loader = new KrakLoader()
        {
            @Override
            public void processNode(Node nd)
            {
                SplashLoader.countNode();
                nodeMap.put(nd.KDV, nd);
            }

            @Override
            public void processEdge(Edge ed)
            {
                SplashLoader.countEdge();
                nodeMap.get(ed.FNODE).addEdge(ed);
                nodeMap.get(ed.TNODE).addEdge(ed);
            }
        };
        try {
            loader.load(dir + "kdv_node_unload.txt", dir + "kdv_unload.txt", nodeMap);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "An unexpected error has occured.\nThis program will exit.",
                    "Error loading",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(System.err);
            System.exit(300);
        }
        DataLine.resetInterner();
        List<Node> n = new LinkedList<>(nodeMap.values());
        nodes = new KDTree(n, lowestX_COORD, lowestY_COORD, highestX_COORD, highestY_COORD);
        height = 621;
        width = 1366;
        reset();
        System.gc();
    }

    /**
     * Sets the maps coordinates to the initial value (Show all Denmark).
     */
    public final void reset()
    {
        bottomY = lowestY_COORD / 1.001;
        topY = highestY_COORD * 1.001;

        // This padding makes sure that the screen will center the map horizontally.
        double padding = ((((topY - bottomY) / height) * width) - (highestX_COORD - lowestX_COORD)) / 2;
        leftX = lowestX_COORD - padding;
        rightX = highestX_COORD + padding;
        calculateFactor();
        setChanged();
    }

    public void goUp(int distance)
    {
        if (topY < highestY_COORD) {
            moveVertical(distance * factor);
        }
        setChanged();
    }

    public void goLeft(int distance)
    {
        if (leftX > lowestX_COORD) {
            moveHorizontal(-distance * factor);
        }
        setChanged();
    }

    public void goRight(int distance)
    {
        if (rightX < highestX_COORD) {
            moveHorizontal(distance * factor);
        }
        setChanged();
    }

    public void goDown(int distance)
    {
        if (bottomY > lowestY_COORD) {
            moveVertical(-distance * factor);
        }
        setChanged();
    }

    public void moveMap(int x, int y)
    {
        moveHorizontal(x * factor);
        moveVertical(-y * factor);
        setChanged();
    }

    /**
     * Zooms in the map.
     */
    public void zoomIn()
    {
        double x = (rightX + leftX) / 2;
        double y = (topY + bottomY) / 2;
        leftX = leftX + (30 * ratioX);
        rightX = rightX - (30 * ratioX);
        topY = topY - (30 * ratioY);
        bottomY = (topY - (rightX - leftX) / ((double) width / (double) height));
        center(x, y);
        calculateFactor();
        setChanged();
    }

    /**
     * Zooms out on the map.
     */
    public void zoomOut()
    {
        double x = (rightX + leftX) / 2;
        double y = (topY + bottomY) / 2;
        leftX = leftX - (30 * ratioX);
        rightX = rightX + (30 * ratioX);
        topY = topY + (30 * ratioY);
        bottomY = (topY - (rightX - leftX) / ((double) width / (double) height));
        center(x, y);
        calculateFactor();
        setChanged();
    }

    /**
     *
     * @param x The screen-x-coordinate for the mouse-pointer.
     * @param y The screen-y-coordinate for the mouse-pointer.
     */
    public void zoomScrollIn(int x, int y)
    {
        // Map coordinates before zoom
        Point.Double p = translatePoint(x, y);
        zoomIn();
        // Map coordinates after zoom
        Point.Double p1 = translatePoint(x, y);

        // Restore the previous map-coordinates to (x, y)
        moveHorizontal(p.x - p1.x);
        moveVertical(p.y - p1.y);
        
        setChanged();
    }

    public void zoomScrollOut(int x, int y)
    {
        // Map coordinates before zoom
        Point.Double p = translatePoint(x, y);
        zoomOut();
        // Map coordinates after zoom
        Point.Double p1 = translatePoint(x, y);

        // Restore the previous map-coordinates to (x, y)
        moveHorizontal(p.x - p1.x);
        moveVertical(p.y - p1.y);

        setChanged();
    }

    /**
     *
     * @param xLeft Screen coordinate for the left side of the rectangle.
     * @param yTop Screen coordinate for the top side of the rectangle.
     * @param xRight Screen coordinate for the right side of the rectangle.
     * @param yBottom Screen coordinate for the bottom side of the rectangle.
     */
    public void zoomRect(int xLeft, int yTop, int xRight, int yBottom)
    {
        Point.Double leftTop = translatePoint(xLeft, yTop), rightBottom = translatePoint(xRight, yBottom);

        double x2 = rightBottom.x, x1 = leftTop.x;
        double y2 = rightBottom.y, y1 = leftTop.y;

        if (x1 > x2) {
            double tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (y2 > y1) {
            double tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
        double ratio = (double) width / (double) height;
        leftX = x1;
        topY = y1;
        if (Math.abs(x2 - x1) / width > Math.abs(y1 - y2) / height) {
            rightX = x2;
            bottomY = (topY - (rightX - leftX) / ratio);
        } else {
            bottomY = y2;
            rightX = leftX + (topY - bottomY) * ratio;
        }
        calculateFactor();
        setChanged();
    }

    public void updateRoadname(int x, int y)
    {
        Point.Double p = translatePoint(x, y);
        Edge near = nodes.getNearest(p.x, p.y);
        if (near != null) {
            roadname = near.VEJNAVN;
        } else {
            roadname = " ";
        }
        setChanged();
    }

    public void setMouseTool(MouseTool mouseTool)
    {
        this.mouseTool = mouseTool;
        setChanged();
    }

    public MouseTool getMouseTool()
    {
        return mouseTool;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        calculateFactor();
    }

    /**
     *
     * @param xLeft
     * @param yBottom
     * @param xRight
     * @param yTop
     * @return
     */
    public List<Node> getNodes(double xLeft, double yBottom, double xRight, double yTop)
    {
        return nodes.getNodes(xLeft, yBottom, xRight, yTop);
    }

    /**
     *
     * @return A Point(x,y) containing the left and top coordinates.
     */
    public Point.Double getLeftTop()
    {
        return new Point.Double(leftX, topY);
    }

    /**
     *
     * @return A point (x,y) containing the right and bottom coordinates.
     */
    public Point.Double getRightBottom()
    {
        return new Point.Double(rightX, bottomY);
    }

    /**
     * Returns the current factor between the map-size and the on-screen
     * map-size.
     *
     * @return The factor used to draw the map in the right
     */
    public double getFactor()
    {
        return factor;
    }

    public void setPressed(Point e)
    {
        pressed = e;
        setChanged();
    }

    public void setDragged(Point e)
    {
        dragged = e;
        setChanged();
    }

    public Point getDragged()
    {
        return dragged;
    }

    public Point getPressed()
    {
        return pressed;
    }

    /**
     *
     * @return The nearest roadname.
     */
    public String getRoadname()
    {
        return roadname;
    }

    /**
     * Calculates the factor that is used to calculate where the roads should be
     * drawn.
     */
    private void calculateFactor()
    {
        // This factor determines how big the Map will be drawn.
        factor = (rightX - leftX) / width;
        if ((topY - bottomY) / height > factor) {
            factor = (topY - bottomY) / height;
        }
        assert (factor != 0);
        ratioX = (rightX - leftX) / width;
        ratioY = (topY - bottomY) / height;
    }

    /**
     * Centers the screen on the map-coordinates supplied.
     *
     * @param x
     * @param y
     */
    private void center(double x, double y)
    {
        double currentCenterX = (rightX + leftX) / 2;
        double currentCenterY = (topY + bottomY) / 2;

        moveHorizontal(x - currentCenterX);
        moveVertical(y - currentCenterY);
    }

    /**
     * Moves the map horizontally.
     *
     * @param distance The "on map"-distance to move the map.
     */
    private void moveHorizontal(double distance)
    {
        leftX += distance;
        rightX += distance;
    }

    /**
     * Moves the map vertically.
     *
     * @param distance The "on map"-distance to move the map.
     */
    private void moveVertical(double distance)
    {
        bottomY += distance;
        topY += distance;
    }

    /**
     * Translates screen-coordinates into map-coordinates.
     */
    private Point.Double translatePoint(int x, int y)
    {
        double xMap = x * factor + leftX;
        double yMap = (height - y) * factor + bottomY;
        return new Point.Double(xMap, yMap);
    }
}
