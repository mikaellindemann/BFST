package dk.itu.groupe;

import dk.itu.groupe.data.*;
import dk.itu.groupe.pathfinding.*;
import dk.itu.groupe.loading.*;
import dk.itu.groupe.util.LinkedList;
import dk.itu.groupe.util.Stack;
import java.awt.Point;
import java.awt.geom.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

/**
 * The model contains all the information about the map.
 *
 * It includes methods to change what part of the map to look at, and
 * zoom-algorithms as well.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class Model extends Observable
{

    private final double lowestX_COORD;
    private final double highestX_COORD;
    private final double lowestY_COORD;
    private final double highestY_COORD;
    private final float minFactor = 0.5f;
    private final int maxNodes;
    private final Map<CommonRoadType, KDTree> treeMap;
    private final String dir;

    private boolean reset, pathByDriveTime;
    private double leftX, bottomY, rightX, topY;
    private double factor;
    private double ratioX;
    private double ratioY;
    private double initialFactor;
    private int from, to;
    private int width, height;
    private ShortestPath shortestPath;
    private Graph g;
    private final Loader loader;
    private MouseTool mouseTool;
    private Node[] nodeMap;
    private Point2D pressed, dragged, moved;
    private String roadname;

    /**
     * On creation of the Model, it will start to load in the data.
     *
     * This takes around 10 seconds on a decent computer. After loading it will
     * create the 2DTree structures for every roadtype in the dataset.
     *
     * @param data
     */
    public Model(String data)
    {
        from = -1;
        to = -1;
        if (data.equals("OpenStreetMap")) {
            dir = "./res/data/osm/";
        } else {
            dir = "./res/data/krak/";
        }
        Loader.Info info = Loader.loadInfo(dir);

        lowestX_COORD = info.xLow;
        lowestY_COORD = info.yLow;
        highestX_COORD = info.xHigh;
        highestY_COORD = info.yHigh;
        maxNodes = info.maxNodes;
        mouseTool = MouseTool.MOVE;
        treeMap = new HashMap<>();
        loader = new Loader();
    }

    public void loadCoastline()
    {
        LinkedList<Edge> edges = loader.loadCoastline("./res/data/coastline/");
        treeMap.put(CommonRoadType.COASTLINE, new KDTree(edges, lowestX_COORD, lowestY_COORD, highestX_COORD, highestY_COORD));

        height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height - 110;
        width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;

        reset();
        initialFactor = factor;
    }

    public void loadNodes()
    {
        g = new Graph(maxNodes);
        nodeMap = loader.loadNodes(dir + "nodes.bin", maxNodes);
    }

    public void loadRoadType(CommonRoadType rt)
    {
        LinkedList<Edge> edgeList = loader.loadEdges(rt, dir, nodeMap);
        if (!edgeList.isEmpty()) {
            treeMap.put(rt, new KDTree(edgeList, lowestX_COORD, lowestY_COORD, highestX_COORD, highestY_COORD));
        }
        for (Edge edge : edgeList) {
            g.addEdge(edge);
        }
    }

    /**
     * Sets the maps coordinates to the initial value (Show all Denmark).
     */
    public final void reset()
    {
        bottomY = lowestY_COORD / 1.001;
        topY = highestY_COORD * 1.001;

        // These paddings make sure that the screen will center the map.
        double xPadding = ((((topY - bottomY) / height) * width) - (highestX_COORD - lowestX_COORD)) / 2;
        double yPadding = ((((rightX - leftX) / width) * height) - (highestY_COORD - lowestY_COORD)) / 2;

        if (xPadding > 0) {
            leftX = lowestX_COORD - xPadding;
            rightX = highestX_COORD + xPadding;
        }

        if (yPadding > 0) {
            bottomY = lowestY_COORD - yPadding;
            topY = highestY_COORD + yPadding;
        }
        reset = true;
        calculateFactor();
        setChanged();
    }

    /**
     * Moves the map <code>distance</code> pixel towards the bottom, to get the
     * feeling that we look at a higher point on the map.
     *
     * @param distance The distance to move the map in pixels.
     */
    public void goUp(double distance)
    {
        reset = false;
        moveVertical(distance);
        setChanged();
    }

    /**
     * Moves the map <code>distance</code> pixel towards the right side, to get
     * the feeling that we look at a point longer to the left on the map.
     *
     * @param distance The distance to move the map in pixels.
     */
    public void goLeft(double distance)
    {
        reset = false;
        moveHorizontal(-distance);
        setChanged();
    }

    /**
     * Moves the map <code>distance</code> pixel towards the left side, to get
     * the feeling that we look at a point longer to the right on the map.
     *
     * @param distance The distance to move the map in pixels.
     */
    public void goRight(double distance)
    {
        reset = false;
        moveHorizontal(distance);
        setChanged();
    }

    /**
     * Moves the map <code>distance</code> pixel towards the top, to get the
     * feeling that we look at a lower point on the map.
     *
     * @param distance The distance to move the map in pixels.
     */
    public void goDown(double distance)
    {
        reset = false;
        moveVertical(-distance);
        setChanged();
    }

    /**
     * Moves the map x pixels horizontally and y pixels vertically.
     *
     * @param x The amount in pixels to move the map in horizontal direction.
     * @param y The amount in pixels to move the map in vertical direction.
     */
    public void moveMap(double x, double y)
    {
        reset = false;
        moveHorizontal(x);
        moveVertical(-y);
        setChanged();
    }

    /**
     * Zooms in the map, and centers to the center point of the current view.
     */
    public void zoomIn()
    {
        if (factor > minFactor) {
            reset = false;
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
    }

    /**
     * Zooms out the map, and centers to the center point of the current view.
     */
    public void zoomOut()
    {
        if (factor < initialFactor) {
            reset = false;
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
    }

    /**
     * Zooms in on the map, and keeps the point specified at the same place on
     * the map after zooming.
     *
     * Google like zooming, so the mouse always point on the same thing on the
     * map.
     *
     * @param x The screen-x-coordinate for the mouse-pointer.
     * @param y The screen-y-coordinate for the mouse-pointer.
     */
    public void zoomInScroll(int x, int y)
    {
        if (factor > minFactor) {
            reset = false;
            // Map coordinates before zoom
            Point2D p = translatePoint(x, y);
            zoomIn();
            // Map coordinates after zoom
            Point2D p1 = translatePoint(x, y);

            // Restore the previous map-coordinates to (x, y)
            moveHorizontal(p.getX() - p1.getX());
            moveVertical(p.getY() - p1.getY());

            setChanged();
        }
    }

    /**
     * Zooms out on the map, and keeps the point specified at the same place on
     * the map after zooming.
     *
     * Google like zooming, so the mouse always point on the same thing on the
     * map.
     *
     * @param x The screen-x-coordinate for the mouse-pointer.
     * @param y The screen-y-coordinate for the mouse-pointer.
     */
    public void zoomOutScroll(int x, int y)
    {
        if (factor < initialFactor) {
            reset = false;
            // Map coordinates before zoom
            Point2D p = translatePoint(x, y);
            zoomOut();
            // Map coordinates after zoom
            Point2D p1 = translatePoint(x, y);

            // Restore the previous map-coordinates to (x, y)
            moveHorizontal(p.getX() - p1.getX());
            moveVertical(p.getY() - p1.getY());

            setChanged();
        }
    }

    /**
     * This zoom-method zooms in to the specified rectangle.
     *
     * If the rectangle doesn't match the ratio between screen width and height,
     * the right or bottom side will be moved to fit.
     *
     * @param xLeft Map coordinate for the left side of the rectangle.
     * @param yTop Screen coordinate for the top side of the rectangle.
     * @param xRight Screen coordinate for the right side of the rectangle.
     * @param yBottom Screen coordinate for the bottom side of the rectangle.
     */
    public void zoomRect(double xLeft, double yTop, double xRight, double yBottom)
    {
        if (factor > minFactor) {
            reset = false;

            double x2 = xRight, x1 = xLeft;
            double y2 = yBottom, y1 = yTop;

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
    }

    /**
     * Updates the field roadname to correspond with the road nearest to the
     * mouse pointer.
     *
     * @param x The on-screen x-coordinate of the mouse.
     * @param y The on-screen y-coordinate of the mouse.
     */
    public void updateRoadname(double x, double y)
    {
        Edge near = nearest(new Point2D.Double(x, y), true);
        // If there are no "nearest" edges
        if (near != null) {
            roadname = near.getRoadname() + " ";
        } else {
            roadname = " ";
        }
        setChanged();
    }

    /**
     * Sets the mouse click/drag-action.
     *
     * As of now this is either drag-to-zoom, or drag-to-move.
     *
     * @param mouseTool The mouse function.
     */
    public void setMouseTool(MouseTool mouseTool)
    {
        this.mouseTool = mouseTool;
        setChanged();
    }

    /**
     * Returns the current mouse function.
     *
     * @return the current mouse function which is either drag-to-zoom or
     * drag-to-move.
     */
    public MouseTool getMouseTool()
    {
        return mouseTool;
    }

    /**
     * Returns the current width used for calculating the view of the map.
     *
     * @return the current width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Returns the current height used for calculating the view of the map.
     *
     * @return the current height.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Sets the size of the map.
     *
     * This should be used whenever the view changes size, so the model can
     * return the correct data.
     *
     * @param width The new width.
     * @param height The new height.
     */
    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        if (reset) {
            reset();
        }
        calculateFactor();
        setChanged();
    }

    /**
     * Returns the edges of roadtype <code>rt</code> and within the specified
     * rectangle-coordinates.
     *
     * @param rt The roadtype of interest.
     * @param xLeft The left x-coordinate.
     * @param yBottom The bottom y-coordinate.
     * @param xRight The right x-coordinate.
     * @param yTop The top y-coordinate.
     * @return A list of edges, containing the edges of roadtype <code>rt</code>
     * within the specified rectangle.
     */
    @SuppressWarnings("unchecked")
    public Set<Edge> getEdges(CommonRoadType rt, double xLeft, double yBottom, double xRight, double yTop)
    {
        if (treeMap.get(rt) != null) {
            return treeMap.get(rt).getEdges(xLeft, yBottom, xRight, yTop); //(xLeft, yBottom, xRight, yTop);
        } else {
            return Collections.EMPTY_SET;
        }
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
        pressed = (e != null) ? translatePoint(e.x, e.y) : null;
        setChanged();
    }

    public void setDragged(Point e)
    {
        dragged = (e != null) ? translatePoint(e.x, e.y) : null;
        setChanged();
    }

    public void setMoved(Point e)
    {
        moved = (e != null) ? translatePoint(e.x, e.y) : null;
        setChanged();
    }

    public void setPathByDriveTime(boolean b)
    {
        pathByDriveTime = b;
        setChanged();
    }

    public boolean getPathByDriveTime()
    {
        return pathByDriveTime;
    }

    public boolean pathPointsSet()
    {
        return from >= 0 && to >= 0;
    }

    public Node fromPoint()
    {
        return from >= 0 ? nodeMap[from] : null;
    }

    public Node toPoint()
    {
        return to >= 0 ? nodeMap[to] : null;
    }

    public void resetPointSet()
    {
        shortestPath = null;
        from = -1;
        to = -1;
        setChanged();
    }

    public void setFromNode(Point2D e) throws NoPathFoundException
    {
        Edge near = nearest(e, false);
        if (near == null) {
            throw new NoPathFoundException("No nearest point was found");
        }
        Node nodeFrom = near.from();
        Node nodeTo = near.to();

        if (new Point.Double(nodeFrom.x(), nodeFrom.y()).distance(e)
                < new Point.Double(nodeTo.x(), nodeTo.y()).distance(e)) {
            from = nodeFrom.id();
        } else {
            from = nodeTo.id();
        }
        setChanged();
    }

    public void setToNode(Point2D e) throws NoPathFoundException
    {
        Edge near = nearest(e, false);
        if (near == null) {
            throw new NoPathFoundException("No nearest point was found");
        }
        Node nodeFrom = near.from();
        Node nodeTo = near.to();

        if (new Point.Double(nodeFrom.x(), nodeFrom.y()).distance(e) < new Point.Double(nodeTo.x(), nodeTo.y()).distance(e)) {
            this.to = nodeFrom.id();
        } else {
            this.to = nodeTo.id();
        }
        setChanged();
    }

    @SuppressWarnings("unchecked")
    public Stack<Edge> getPath() throws NoPathFoundException
    {
        shortestPath = new ShortestPath(g, from, to, pathByDriveTime, nodeMap);
        if (shortestPath.hasPathTo(to)) {
            setChanged();
            return shortestPath.pathTo(to);
        }
        setChanged();
        throw new NoPathFoundException("No path was found");
    }

    public Point2D getDragged()
    {
        return dragged;
    }

    public Point2D getPressed()
    {
        return pressed;
    }

    public Point2D getMoved()
    {
        return moved;
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
        double centerX = (rightX + leftX) / 2;
        if (distance > 0 && centerX < highestX_COORD || distance < 0 && centerX > lowestX_COORD) {
            leftX += distance;
            rightX += distance;
        }
    }

    /**
     * Moves the map vertically.
     *
     * @param distance The "on map"-distance to move the map.
     */
    private void moveVertical(double distance)
    {
        double centerY = (topY + bottomY) / 2;
        if (distance > 0 && centerY < highestY_COORD || distance < 0 && centerY > lowestY_COORD) {
            bottomY += distance;
            topY += distance;
        }
    }

    private Edge nearest(Point2D p, boolean factorAware)
    {
        LinkedList<Edge> edges = new LinkedList<>();
        for (CommonRoadType rt : CommonRoadType.values()) {
            if (rt == CommonRoadType.PLACES || rt == CommonRoadType.COASTLINE) {
                continue;
            }
            if ((!factorAware || rt.isEnabled(factor)) && treeMap.get(rt) != null) {
                Edge e = treeMap.get(rt).getNearest(p.getX(), p.getY());
                if (e != null) {
                    edges.add(e);
                }
            }
        }

        Edge near = null;
        double dist = Double.MAX_VALUE;
        for (Edge edge : edges) {
            Point2D start = null;
            Point2D last = null;
            for (PathIterator pi = edge.getShape().getPathIterator(null); !pi.isDone(); pi.next()) {
                double[] coords = new double[6];
                int type = pi.currentSegment(coords);
                switch (type) {
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
                            near = edge;
                        }
                        break;
                    case PathIterator.SEG_CLOSE:
                        line = new Line2D.Double(last, start);
                        d = line.ptSegDist(p);
                        if (d < dist) {
                            dist = d;
                            near = edge;
                        }
                        break;
                }
            }
        }
        return near;
    }

    /**
     * Translates screen-coordinates into map-coordinates.
     *
     * @param x
     * @param y
     * @return
     */
    public Point2D translatePoint(int x, int y)
    {
        double xMap = x * factor + leftX;
        double yMap = (height - y) * factor + bottomY;
        return new Point2D.Double(xMap, yMap);
    }
}
