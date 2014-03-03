package dk.itu.groupe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class Map extends JComponent implements MouseListener, MouseMotionListener {

    // These are the lowest and highest coordinates in the dataset.
    // If we change dataset, these are likely to change.
    private final static int lowestX_COORD = 442254;
    private final static int highestX_COORD = 892658;
    private final static int lowestY_COORD = 6049914;
    private final static int highestY_COORD = 6402050;
    private static String xTrack;
    private static String yTrack;

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

    public Map() throws IOException {
        String dir = "./data/";

        lowX = lowestX_COORD;
        lowY = lowestY_COORD;
        highX = highestX_COORD;
        highY = highestY_COORD;

        // For this example, we'll simply load the raw data into
        // ArrayLists.
        //final List<EdgeData> edgeList = new ArrayList<>();
        nodeMap = new HashMap<>();
        final Set<EdgeData> edgeSet = new HashSet<>();

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
                //edgeList.add(ed);
                edgeSet.add(ed);
            }
        };

        // If your machine slows to a crawl doing inputting, try
        // uncommenting this. 
        // Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        // Invoke the loader class.
        loader.load(dir + "kdv_node_unload.txt",
                dir + "kdv_unload.txt");

        edges = new KDTree(edgeSet);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1300, 700);
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
        //System.out.println(factor);
    }

    @Override
    public void paintComponent(Graphics g) {
        calculateFactor();
        for (EdgeData edge : edges.getEdges(lowX, lowY, highX, highY)) {

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
                    g.setColor(Color.MAGENTA);
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

            int fx = (int) ((nodeMap.get(edge.FNODE).X_COORD - lowX) / factor);
            int fy = getHeight() - (int) ((nodeMap.get(edge.FNODE).Y_COORD - lowY) / factor);
            int lx = (int) ((nodeMap.get(edge.TNODE).X_COORD - lowX) / factor);
            int ly = getHeight() - (int) ((nodeMap.get(edge.TNODE).Y_COORD - lowY) / factor);

            g.drawLine(fx, fy, lx, ly);
        }
        System.gc();
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
        lowY = lowY + (30 * factor);
        highY = highY + (30 * factor);
        repaint();
    }

    public void goLeft() {
        lowX = lowX - (30 * factor);
        highX = highX + (30 * factor);
        repaint();
    }

    public void goRight() {
        lowX = lowX + (30 * factor);
        highX = highX - (30 * factor);
        repaint();
    }

    public void goDown() {
        lowY = lowY - (30 * factor);
        highY = highY - (30 * factor);
        repaint();
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
        if (startX < stopX && startY < stopY) {
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
    }

    //Tracks exact position of mouse pointer
    private void trackMouse(double xTrack, double yTrack) {
        double mapX = xTrack * factor + lowX;
        double mapY = (getHeight() - yTrack) * factor + lowY;
        //System.out.println("x:" + xTrack + "| y:" + yTrack);
        String x = "" + (Math.round(mapX * 10) / 10.0);
        String y = "" + (Math.round(mapY * 10) / 10.0);
        String pointer = edges.nearest(mapX, mapY).VEJNAVN;
        if (pointer != null) {
            gui.label.setText(pointer);
        }
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
        trackMouse(me.getX(), me.getY());
    }
}
