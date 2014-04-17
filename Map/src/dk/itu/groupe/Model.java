package dk.itu.groupe;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

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

    // These are the lowest and highest coordinates in the dataset.
    // If we change dataset, these are likely to change.
    private final double lowestX_COORD;
    private final double highestX_COORD;
    private final double lowestY_COORD;
    private final double highestY_COORD;

    // Bounds of the window.
    private double leftX, bottomY, rightX, topY;
    private double factor;
    private double ratioX;
    private double ratioY;
    private final float minFactor = 0.5f;

    private Map<CommonRoadType, KDTree> treeMap;

    private String roadname;

    private Point pressed, dragged;
    private MouseTool mouseTool;

    private int width, height;

    private boolean reset;
    public final boolean osm;

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
        double xLow = 0, yLow = 0, xHigh = 0, yHigh = 0;
        int maxNodes = 0, maxEdges = 0;
        String dir;
        if (data.equals("OpenStreetMap")) {
            dir = "./res/data/osm/";
            osm = true;
        } else {
            dir = "./res/data/krak/";
            osm = false;
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "info.csv")));
            xLow = Double.parseDouble(br.readLine());
            yLow = Double.parseDouble(br.readLine());
            xHigh = Double.parseDouble(br.readLine());
            yHigh = Double.parseDouble(br.readLine());
            maxNodes = Integer.parseInt(br.readLine());
            maxEdges = Integer.parseInt(br.readLine());
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        final LoadingFrame lf = new LoadingFrame(maxNodes, maxEdges);
        lowestX_COORD = xLow;
        lowestY_COORD = yLow;
        highestX_COORD = xHigh;
        highestY_COORD = yHigh;

        treeMap = new HashMap<>();

        mouseTool = MouseTool.MOVE;
        final List<Edge> coastlines = new LinkedList<>();
        final Map<Long, Node> nodeMap = new HashMap<>();
        final Map<CommonRoadType, List<Edge>> edgeMap = new HashMap<>();
        for (CommonRoadType rt : CommonRoadType.values()) {
            edgeMap.put(rt, new LinkedList<Edge>());
        }
        Loader loader = new Loader()
        {
            @Override
            public void processNode(Node nd)
            {
                nodeMap.put(nd.ID, nd);
                lf.countNode();
            }

            @Override
            public void processEdge(Edge ed)
            {
                edgeMap.get(ed.getType()).add(ed);
                lf.countEdge();
            }

            @Override
            public void processCoastline(Coastline cl)
            {
                coastlines.add(cl);
            }
        };
        try {
            loader.load(dir + "nodes.csv", dir + "edges.csv", nodeMap);
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

        ExecutorService es = Executors.newFixedThreadPool(4);

        for (final CommonRoadType rt : CommonRoadType.values()) {
            if (rt == CommonRoadType.COASTLINE) {
                es.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        treeMap.put(rt, new KDTree(coastlines, lowestX_COORD, lowestY_COORD, highestX_COORD, highestY_COORD));
                    }
                });
            } else {
                es.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (edgeMap.get(rt).size() > 0) {
                            treeMap.put(rt, new KDTree(edgeMap.get(rt), lowestX_COORD, lowestY_COORD, highestX_COORD, highestY_COORD));
                        }
                    }
                });
            }
        }
        try {
            es.shutdown();
            es.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
        height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height - 100;
        width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        reset();
        lf.dispose();
        System.gc();
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
    public void goUp(int distance)
    {
        reset = false;
        if (topY < highestY_COORD) {
            moveVertical(distance * factor);
        }
        setChanged();
    }

    /**
     * Moves the map <code>distance</code> pixel towards the right side, to get
     * the feeling that we look at a point longer to the left on the map.
     *
     * @param distance The distance to move the map in pixels.
     */
    public void goLeft(int distance)
    {
        reset = false;
        if (leftX > lowestX_COORD) {
            moveHorizontal(-distance * factor);
        }
        setChanged();
    }

    /**
     * Moves the map <code>distance</code> pixel towards the left side, to get
     * the feeling that we look at a point longer to the right on the map.
     *
     * @param distance The distance to move the map in pixels.
     */
    public void goRight(int distance)
    {
        reset = false;
        if (rightX < highestX_COORD) {
            moveHorizontal(distance * factor);
        }
        setChanged();
    }

    /**
     * Moves the map <code>distance</code> pixel towards the top, to get the
     * feeling that we look at a lower point on the map.
     *
     * @param distance The distance to move the map in pixels.
     */
    public void goDown(int distance)
    {
        reset = false;
        if (bottomY > lowestY_COORD) {
            moveVertical(-distance * factor);
        }
        setChanged();
    }

    public boolean isOsm()
    {
        return osm;
    }

    /**
     * Moves the map x pixels horizontally and y pixels vertically.
     *
     * @param x The amount in pixels to move the map in horizontal direction.
     * @param y The amount in pixels to move the map in vertical direction.
     */
    public void moveMap(int x, int y)
    {
        reset = false;
        moveHorizontal(x * factor);
        moveVertical(-y * factor);
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
            Point.Double p = translatePoint(x, y);
            zoomIn();
            // Map coordinates after zoom
            Point.Double p1 = translatePoint(x, y);

            // Restore the previous map-coordinates to (x, y)
            moveHorizontal(p.x - p1.x);
            moveVertical(p.y - p1.y);

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
        reset = false;
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
     * This zoom-method zooms in to the specified rectangle.
     *
     * If the rectangle doesn't match the ratio between screen width and height,
     * the right or bottom side will be moved to fit.
     *
     * @param xLeft Screen coordinate for the left side of the rectangle.
     * @param yTop Screen coordinate for the top side of the rectangle.
     * @param xRight Screen coordinate for the right side of the rectangle.
     * @param yBottom Screen coordinate for the bottom side of the rectangle.
     */
    public void zoomRect(int xLeft, int yTop, int xRight, int yBottom)
    {
        if (factor > minFactor) {
            reset = false;
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
    }

    /**
     * Updates the field roadname to correspond with the road nearest to the
     * mouse pointer.
     *
     * @param x The on-screen x-coordinate of the mouse.
     * @param y The on-screen y-coordinate of the mouse.
     */
    public void updateRoadname(int x, int y)
    {
        Point.Double p = translatePoint(x, y);
        List<Edge> edges = new LinkedList<>();
        for (CommonRoadType rt : CommonRoadType.values()) {
            KDTree tree = treeMap.get(rt);
            if (tree != null) {
                Edge e = tree.getNearest(p.x, p.y);
                if (e != null) {
                    edges.add(e);
                }
            }
        }
        Edge near = null;
        double dist = Double.MAX_VALUE;
        for (Edge edge : edges) {
            if (edge.getLine().ptSegDist(p) < dist) {
                dist = edge.getLine().ptSegDist(p);
                near = edge;
            }
        }
        // If there are no "nearest" edges
        if (near != null) {
            roadname = near.getRoadname();
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
    public List<Edge> getEdges(CommonRoadType rt, double xLeft, double yBottom, double xRight, double yTop)
    {
        if (treeMap.get(rt) != null) {
            return treeMap.get(rt).getEdges(xLeft, yBottom, xRight, yTop);
        } else {
            return Collections.EMPTY_LIST;
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
