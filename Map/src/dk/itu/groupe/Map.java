package dk.itu.groupe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class Map extends JComponent implements MouseListener, MouseMotionListener {

    // These are the lowest and highest coordinates in the dataset.
    // If we change dataset, these are likely to change.
    private final static double lowestX_COORD = 442254.35659;
    private final static double highestX_COORD = 892658.21706;
    private final static double lowestY_COORD = 6049914.43018;
    private final static double highestY_COORD = 6402050.98297;

    // Bounds of the window.
    private double lowX, lowY, highX, highY;
    private double factor;

    private static GUI gui;

    private MouseEvent pressed, released;

    /**
     * An ArrayList of EdgeData containing (for now) all the data supplied.
     */
    private final KDTree edges;

    /**
     * A HashMap that links a NodeData's KDV-number to the NodeData itself.
     *
     * This way we can get the specified NodeData from the EdgeDatas FNODE and
     * TNODE-fields.
     */
    static HashMap<Integer, NodeData> nodeMap;

    public Map() {
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
        KrakLoader loader = new KrakLoader() {
            @Override
            public void processNode(NodeData nd) {
                nodeMap.put(nd.KDV, nd);
            }

            @Override
            public void processEdge(EdgeData ed) {
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
    public Dimension getPreferredSize() {
        return new Dimension(1000, 600);
    }

    private void calculateFactor() {
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
    }

    @Override
    public void paintComponent(Graphics g) {
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
        }
    }

    public static void main(String[] args) throws IOException {

        gui = new GUI();
        gui.gui();

        Timer t = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
                System.out.printf("Heap memory usage: %d MB\r",
                        mxbean.getHeapMemoryUsage().getUsed() / (1000000));
            }
        });
        t.start();
    }

    public void reset() {
        lowX = lowestX_COORD;
        lowY = lowestY_COORD;
        highX = highestX_COORD;
        highY = highestY_COORD;
        repaint();
    }

    public void goUp() {
        if (highY < highestY_COORD) {
            lowY = lowY + (30 * factor);
            highY = highY + (30 * factor);
            repaint();
        }
    }

    public void goLeft() {
        if (lowX > lowestX_COORD) {
            lowX = lowX - (30 * factor);
            highX = highX - (30 * factor);
            repaint();
        }
    }

    public void goRight() {
        if (highX < highestX_COORD) {
            lowX = lowX + (30 * factor);
            highX = highX + (30 * factor);
            repaint();
        }
    }

    public void goDown() {
        if (lowY > lowestY_COORD) {
            lowY = lowY - (30 * factor);
            highY = highY - (30 * factor);
            repaint();
        }
    }

    public void ZoomIn() {
        lowX = lowX + (30 * factor);
        lowY = lowY + (30 * factor);
        highX = highX - (30 * factor);
        highY = highY - (30 * factor);
        repaint();
    }

    public void ZoomOut() {
        lowX = lowX - (30 * factor);
        lowY = lowY - (30 * factor);
        highX = highX + (30 * factor);
        highY = highY + (30 * factor);
        repaint();
    }

    private void zoomRect(double startX, double startY, double stopX, double stopY) {
        if (startX > stopX) {
            double tmp = startX;
            startX = stopX;
            stopX = tmp;
        }
        if (startY > stopY) {
            double tmp = startY;
            startY = stopY;
            stopY = tmp;
        }
        //throw new UnsupportedOperationException("Not yet implemented");
            /*System.out.println("Pressed startXY: " + startX + " " + startY);
         System.out.println("Pressed stopXY: " + stopX + " " + stopY);

         System.out.println("Pressed high: " + highX + " " + highY);
         System.out.println("Pressed low: " + lowX + " " + lowY);*/

        highX = (lowX + (stopX * factor));
        highY = (lowY + ((getHeight() - startY) * factor));
        lowX = (lowX + (startX * factor));
        lowY = (lowY + ((getHeight() - stopY) * factor));
        repaint();

        /*System.out.println("Pressed high: " + highX + " " + highY);
         System.out.println("Pressed low: " + lowX + " " + lowY);*/
    }

    @Override
    public void mouseClicked(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    public void mouseOver(final MouseEvent me) {

    }

    @Override
    public void mousePressed(MouseEvent me) {
        //Right click to reset.
        if (me.getButton() == 3) {
            reset();
        } else {
            pressed = me;
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (me.getButton() == 1) {
            released = me;
            if (pressed.getX() == released.getX() && pressed.getY() == released.getY()) {
                return;
            }
            zoomRect(pressed.getX(), pressed.getY(), released.getX(), released.getY());
            pressed = null;
            released = null;
        }
    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent me) {
        double mapX = me.getX() * factor + lowX;
        double mapY = (getHeight() - me.getY()) * factor + lowY;
        
        EdgeData near = edges.getNearest(mapX, mapY);
        if (near != null) {
            String pointer = near.VEJNAVN;
            gui.label.setText(pointer);
        } else {
            gui.label.setText("");
        }
    }
}
