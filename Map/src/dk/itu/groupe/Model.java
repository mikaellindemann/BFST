package dk.itu.groupe;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
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
    private double lowX, lowY, highX, highY;
    private double factor;
    private double ratioX;
    private double ratioY;

    // mouse positions
    private double mapX, mapY;
    private double mapXPressed, mapYPressed;

    private String roadname;

    private MouseEvent pressed, released, dragged;
    private MouseTool mouse;

    private int width, height;

    /**
     * An ArrayList of EdgeData containing (for now) all the data supplied.
     */
    private final KDTree edges;

    public Model()
    {
        String dir = "./data/";

        lowX = lowestX_COORD;
        lowY = lowestY_COORD;
        highX = highestX_COORD;
        highY = highestY_COORD;
        mouse = MouseTool.ZOOM;

        // For this example, we'll simply load the raw data into
        // ArrayLists.
        //final List<EdgeData> edgeList = new ArrayList<>();
        final HashMap<Integer, Node> nodeMap = new HashMap<>();
        final List<Edge> edgeList = new LinkedList<>();

        // For that, we need to inherit from KrakLoader and override
        // processNode and processEdge. We do that with an 
        // anonymous class. 
        KrakLoader loader = new KrakLoader()
        {
            @Override
            public void processNode(Node nd)
            {
                nodeMap.put(nd.KDV, nd);
            }

            @Override
            public void processEdge(Edge ed)
            {
                edgeList.add(ed);
            }
        };

        // If your machine slows to a crawl doing inputting, try
        // uncommenting this. 
        // Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        // Invoke the loader class.
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
        edges = new KDTree(edgeList, lowestX_COORD, lowestY_COORD, highestX_COORD, highestY_COORD);
        height = 600;
        width = (int) (height * (highestX_COORD - lowestX_COORD) / (highestY_COORD - lowestY_COORD));
    }

    /**
     * Calculates the factor that is used to calculate where the roads should be
     * drawn.
     */
    public void calculateFactor()
    {
        // This factor determines how big the Map will be drawn.
        factor = (highX - lowX) / width;
        if ((highY - lowY) / height > factor) {
            factor = (highY - lowY) / height;
        }
        if (factor == 0) {
            System.err.println("low: (" + lowX + ", " + lowY + ")");
            System.err.println("high: (" + highX + ", " + highY + ")");
            System.err.println("Window: (" + width + ", " + height + ")");
        }
        ratioX = (highX - lowX) / width;
        ratioY = (highY - lowY) / height;
    }

    /**
     * Sets the maps coordinates to the initial value (Show all Denmark).
     */
    public void reset()
    {
        lowX = lowestX_COORD;
        lowY = lowestY_COORD;
        highX = highestX_COORD;
        highY = highestY_COORD;
        setChanged();
    }

    public void goUp(int distance)
    {
        if (highY < highestY_COORD) {
            lowY = lowY + (distance * factor);
            highY = highY + (distance * factor);
        }
        setChanged();
    }

    public void goLeft(int distance)
    {
        if (lowX > lowestX_COORD) {
            lowX = lowX - (distance * factor);
            highX = highX - (distance * factor);
        }
        setChanged();
    }

    public void goRight(int distance)
    {
        if (highX < highestX_COORD) {
            lowX = lowX + (distance * factor);
            highX = highX + (distance * factor);
        }
        setChanged();
    }

    public void goDown(int distance)
    {
        if (lowY > lowestY_COORD) {
            lowY = lowY - (distance * factor);
            highY = highY - (distance * factor);
        }
        setChanged();
    }

    public void moveMap(int x, int y)
    {
        if (x > 0) {
            goRight(x);
        } else {
            goLeft(-x);
        }
        if (y > 0) {
            goDown(y);
        } else {
            goUp(-y);
        }
        setChanged();
    }

    /**
     * Zooms in the map.
     */
    public void zoomIn()
    {
        lowX = lowX + (30 * ratioX);
        highX = highX - (30 * ratioX);
        highY = highY - (30 * ratioY);
        lowY = (highY - (highX - lowX) / ((double) width / (double) height));
        setChanged();
    }

    /**
     * Zooms out on the map.
     */
    public void zoomOut()
    {
        lowX = lowX - (30 * ratioX);
        highX = highX + (30 * ratioX);
        highY = highY + (30 * ratioY);
        lowY = (highY - (highX - lowX) / ((double) width / (double) height));
        setChanged();
    }

    public void zoomScrollIn(double usp, double dsp, double lsp, double rsp)
    {
        lowX = lowX + (60 * lsp * ratioX);
        highX = highX - (60 * rsp * ratioX);
        highY = highY - (60 * usp * ratioY);
        lowY = (highY - (highX - lowX) / ((double) width / (double) height));
        setChanged();
    }

    public void zoomScrollOut(double usp, double dsp, double lsp, double rsp)
    {
        lowX = lowX - (60 * lsp * ratioX);
        highX = highX + (60 * rsp * ratioX);
        highY = highY + (60 * usp * ratioY);
        lowY = (highY - (highX - lowX) / ((double) width / (double) height));
        setChanged();
    }

    public void zoomRect()
    {
        double x2 = mapX, x1 = mapXPressed;
        double y2 = mapY, y1 = mapYPressed;

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
        lowX = x1;
        highY = y1;
        if (Math.abs(x2 - x1) / width > Math.abs(y1 - y2) / height) {
            // This is buggy
            highX = x2;
            lowY = (highY - (highX - lowX) / ratio);
        } else {
            // This should work
            lowY = y2;
            highX = lowX + (highY - lowY) * ratio;
        }
        setChanged();
    }

    public void setMouseMapCoordinates(int x, int y)
    {
        mapX = x * factor + lowX;
        mapY = (height - y) * factor + lowY;
    }

    public void updateRoadname()
    {
        Edge near = edges.getNearest(mapX, mapY);
        if (near != null) {
            roadname = near.VEJNAVN;
        } else {
            roadname = " ";
        }
        setChanged();
    }

    public void setMouse(MouseTool mouse)
    {
        this.mouse = mouse;
        setChanged();
    }

    public MouseTool getMouse()
    {
        return mouse;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setSize(Dimension d)
    {
        width = d.width;
        height = d.height;
    }

    public List<Edge> getEdges(double xLow, double yLow, double xHigh, double yHigh)
    {
        return edges.getEdges(xLow, yLow, xHigh, yHigh);
    }

    /**
     *
     * @return A Point(x,y) containing the top left coordinates.
     */
    public Point.Double getTopLeft()
    {
        return new Point.Double(lowX, highY);
    }

    /**
     *
     * @return A point (x,y) containing the bottom right coordinates.
     */
    public Point.Double getBottomRight()
    {
        return new Point.Double(highX, lowY);
    }

    public double getFactor()
    {
        return factor;
    }

    public void setPressed(MouseEvent pressed)
    {
        this.pressed = pressed;
        if (pressed != null) {
            mapXPressed = pressed.getX() * factor + lowX;
            mapYPressed = (height - pressed.getY()) * factor + lowY;
        }
    }

    public void setReleased(MouseEvent released)
    {
        this.released = released;
        setChanged();
    }

    public void setDragged(MouseEvent dragged)
    {
        this.dragged = dragged;
        setChanged();
    }

    public static double getLowestX_COORD()
    {
        return lowestX_COORD;
    }

    public static double getHighestX_COORD()
    {
        return highestX_COORD;
    }

    public static double getLowestY_COORD()
    {
        return lowestY_COORD;
    }

    public static double getHighestY_COORD()
    {
        return highestY_COORD;
    }

    public double getMapX()
    {
        return mapX;
    }

    public double getMapY()
    {
        return mapY;
    }

    public MouseEvent getPressed()
    {
        return pressed;
    }

    public MouseEvent getReleased()
    {
        return released;
    }

    public MouseEvent getDragged()
    {
        return dragged;
    }

    public String getRoadname()
    {
        return roadname;
    }
}
