package dk.itu.groupe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class Map extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener
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

    private static GUI gui;
    
    private MouseEvent pressed, released, dragged;

    /**
     * An ArrayList of EdgeData containing (for now) all the data supplied.
     */
    private final KDTree edges;

    /**
     * A HashMap that links a NodeData's KDV-number to the NodeData itself.
     *
     * This way we can get the specified NodeData from the EdgeDatas FNODE and
     * TNODE-fields.
     * This map is erased at the end of the constructor.
     */
    static HashMap<Integer, NodeData> nodeMap;

    public Map()
    {
        String dir = "./data/";

        lowX = lowestX_COORD;
        lowY = lowestY_COORD;
        highX = highestX_COORD;
        highY = highestY_COORD;

        // For this example, we'll simply load the raw data into
        // ArrayLists.
        //final List<EdgeData> edgeList = new ArrayList<>();
        nodeMap = new HashMap<>();
        final List<EdgeData> edgeList = new LinkedList<>();

        // For that, we need to inherit from KrakLoader and override
        // processNode and processEdge. We do that with an 
        // anonymous class. 
        KrakLoader loader = new KrakLoader()
        {
            @Override
            public void processNode(NodeData nd)
            {
                nodeMap.put(nd.KDV, nd);
            }

            @Override
            public void processEdge(EdgeData ed)
            {
                edgeList.add(ed);
            }
        };

        // If your machine slows to a crawl doing inputting, try
        // uncommenting this. 
        // Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        // Invoke the loader class.
        try {
            loader.load(dir + "kdv_node_unload.txt",
                    dir + "kdv_unload.txt");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "An unexpected error has occured.\nThis program will exit.",
                    "Error loading",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(System.err);
            System.exit(300);
        }

        edges = new KDTree(edgeList, lowestX_COORD, lowestY_COORD, highestX_COORD, highestY_COORD);
        nodeMap = null;
        System.gc();
    }

    @Override
    public Dimension getPreferredSize()
    {
        double ratio = (highestX_COORD - lowestX_COORD) / (highestY_COORD - lowestY_COORD);
        int height = 670;
        int width = (int) (height * ratio);
        
        return new Dimension(width, height);
    }

    /**
     * Calculates the factor that is used to calculate where the roads should be drawn.
     */
    private void calculateFactor()
    {
        // This factor determines how big the Map will be drawn.
        factor = (highX - lowX) / getWidth();
        if ((highY - lowY) / getHeight() > factor) {
            factor = (highY - lowY) / getHeight();
        }
        if (factor == 0) {
            System.err.println("low: (" + lowX + ", " + lowY + ")");
            System.err.println("high: (" + highX + ", " + highY + ")");
            System.err.println("Window: (" + getWidth() + ", " + getHeight() + ")");
        }
        ratioX = (highX - lowX) / getWidth();
        ratioY = (highY - lowY) / getHeight();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        calculateFactor();
        List<EdgeData> edgess = edges.getEdges(lowX, lowY, highX, highY);
        for (EdgeData edge : edgess) {
            switch (edge.TYP) {
                case (1):
                case (21):
                case (31):
                    g.setColor(Color.RED);
                    break;
                case (2):
                case (22):
                case (32):
                    g.setColor(Color.GRAY);
                    break;
                case (3):
                case (23):
                case (33):
                    g.setColor(Color.YELLOW);
                    break;
                case (4):
                case (5):
                case (6):
                case (24):
                case (25):
                case (26):
                case (34):
                case (35):
                    g.setColor(Color.GRAY);
                    break;
                case (8):
                case (10):
                case (28):
                    g.setColor(Color.LIGHT_GRAY);
                    break;
                case (11):
                    g.setColor(Color.BLACK);
                    break;
                case (41):
                case (42):
                case (43):
                case (44):
                case (45):
                case (46):
                case (48):
                    g.setColor(Color.GREEN);
                    break;
                case (80):
                    continue;
                case (99):
                    continue;
                default:
                    g.setColor(Color.BLACK);
            }

            int fx = (int) ((edge.line.getX1() - lowX) / factor);
            int fy = getHeight() - (int) ((edge.line.getY1() - lowY) / factor);
            int lx = (int) ((edge.line.getX2() - lowX) / factor);
            int ly = getHeight() - (int) ((edge.line.getY2() - lowY) / factor);

            g.drawLine(fx, fy, lx, ly);
            if (dragged != null && pressed != null) {
                g.setColor(Color.BLACK);

                int x1 = pressed.getX();
                int x2 = dragged.getX();
                int y1 = pressed.getY();
                int y2 = dragged.getY();

                double x = Math.abs(x2 - x1) / (double) getWidth();
                double y = Math.abs(y2 - y1) / (double) getHeight();

                if (x > y) {
                    int side = (int) ((x2 - x1) / ((double) getWidth() / getHeight()));
                    g.drawRect(x1, y1, x2 - x1, side);
                } else {
                    int side = (int) ((y2 - y1) * ((double) getWidth() / getHeight()));
                    g.drawRect(x1, y1, side, y2 - y1);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        Timer t = new Timer(1000, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
                System.out.printf("Heap memory usage: %d MB\r",
                        mxbean.getHeapMemoryUsage().getUsed() / (1000000));
            }
        });
        t.start();
        gui = new GUI();
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
        repaint();
    }

    public void goUp()
    {
        if (highY < highestY_COORD) {
            lowY = lowY + (30 * factor);
            highY = highY + (30 * factor);
            repaint();
        }
    }

    public void goLeft()
    {
        if (lowX > lowestX_COORD) {
            lowX = lowX - (30 * factor);
            highX = highX - (30 * factor);
            repaint();
        }
    }

    public void goRight()
    {
        if (highX < highestX_COORD) {
            lowX = lowX + (30 * factor);
            highX = highX + (30 * factor);
            repaint();
        }
    }

    public void goDown()
    {
        if (lowY > lowestY_COORD) {
            lowY = lowY - (30 * factor);
            highY = highY - (30 * factor);
            repaint();
        }
    }

    /**
     * Zooms in the map.
     */
    public void zoomIn()
    {
        lowX = lowX + (30 * ratioX);
        highX = highX - (30 * ratioX);
        highY = highY - (30 * ratioY);
        lowY = (highY - (highX - lowX) / ((double) getWidth() / (double) getHeight()));
        repaint();
    }

    /**
     * Zooms out on the map.
     */
    public void zoomOut()
    {
        lowX = lowX - (30 * ratioX);
        highX = highX + (30 * ratioX);
        highY = highY + (30 * ratioY);
        lowY = (highY - (highX - lowX) / ((double) getWidth() / (double) getHeight()));
        repaint();
    }

    private void zoomScrollIn(double lsp, double dsp, double rsp, double usp) 
    {
        lowX = lowX + (60 * lsp * factor);
        highX = highX - (60 * rsp * factor);
        highY = highY - (60 * usp * factor);
        lowY = (highY - (highX - lowX) / ((double) getWidth() / (double) getHeight()));
        repaint();
    }    

    private void zoomScrollOut(double lsp, double dsp, double rsp, double usp) 
    {
        lowX = lowX - (60 * lsp* factor);
        highX = highX + (60 * rsp * factor);
        highY = highY + (60 * usp * factor);
        lowY = (highY - (highX - lowX) / ((double) getWidth() / (double) getHeight()));
        repaint();
    }

    private void zoomRect()
    {
        double xFactor = Math.abs((mapX - mapXPressed) / (double) getWidth());
        double yFactor = Math.abs((mapYPressed - mapY) / (double) getHeight());

        double xLeft = mapXPressed, xRight = mapX;
        double yUp = mapYPressed, yDown = mapY;

        if (xLeft > xRight) {
            double tmp = xRight;
            xRight = xLeft;
            xLeft = tmp;
        }
        if (yDown > yUp) {
            double tmp = yUp;
            yUp = yDown;
            yDown = tmp;
        }

        lowX = xLeft;
        highY = yUp;

        if (xFactor > yFactor) {
            highX = xRight;
            lowY = highY - (mapX - mapXPressed) / ((double) getWidth() / (double) getHeight());
        } else {
            lowY = yDown;
            highX = (highY - lowY) * ((double) getWidth() / (double) getHeight());
        }
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        setMouseMapCoordinates(e.getX(), e.getY());
        // Only zoom if the mouse is within the actual map
        if (mapX < highX && mapX > lowX && mapY < highY && mapY > lowY) {
            // calculate the ratio of the distances from the mouse to the edges (up, down, left, right)
            double ls = mapX-lowX;
            double rs = highX-mapX;
            double lsp = ls/(ls+rs);
            double rsp = rs/(ls+rs);
            double ds = mapY-lowY;
            double us = highY-mapY;
            double dsp = ds/(ds+us);
            double usp = us/(ds+us);
            if(e.getWheelRotation() < 0) {
                zoomScrollIn(lsp, dsp, rsp, usp);
                
            } else {
                zoomScrollOut(lsp, dsp, rsp, usp);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent me)
    {

    }

    @Override
    public void mouseEntered(MouseEvent me)
    {

    }

    @Override
    public void mousePressed(MouseEvent me)
    {
        //Right click to reset.
        if (me.getButton() == 3) {
            reset();
        } else {
            mapXPressed = me.getX() * factor + lowX;
            mapYPressed = (getHeight() - me.getY()) * factor + lowY;
            pressed = me;
        }
    }

    @Override
    public void mouseReleased(MouseEvent me)
    {
        if (me.getButton() == 1) {
            released = me;
            if (pressed.getX() == released.getX() && pressed.getY() == released.getY()) {
                released = null;
                pressed = null;
                return;
            }
            zoomRect();
            released = null;
            pressed = null;
        }
    }

    @Override
    public void mouseExited(MouseEvent me)
    {

    }

    @Override
    public void mouseDragged(MouseEvent me)
    {
        setMouseMapCoordinates(me.getX(), me.getY());
        dragged = me;
        repaint();
    }

    private void setMouseMapCoordinates(int x, int y) 
    {
        mapX = x * factor + lowX;
        mapY = (getHeight() - y) * factor + lowY;
    }
    
    @Override
    public void mouseMoved(MouseEvent me)
    {
        setMouseMapCoordinates(me.getX(), me.getY());
        updateRoadName();
    }
    
    private void updateRoadName()
    {
        EdgeData near = edges.getNearest(mapX, mapY);
        if (near != null) {
            String pointer = near.VEJNAVN;
            gui.roadName.setText(pointer);
        } else {
            gui.roadName.setText(" ");
        }
    }
}
