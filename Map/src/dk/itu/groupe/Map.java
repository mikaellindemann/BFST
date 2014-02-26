package dk.itu.groupe;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Map extends JComponent implements MouseListener, MouseMotionListener {

    // These are the lowest and highest coordinates in the dataset.
    // If we change dataset, these are likely to change.
    private final static int lowestX_COORD = 442254;
    private final static int highestX_COORD = 892658;
    private final static int lowestY_COORD = 6049914;
    private final static int highestY_COORD = 6402050;

    // Bounds of the window.
    private int lowX, lowY, highX, highY;
    private double factor;
    
    private MouseEvent pressed, released;
    
    private int trackX;
    private int trackY;

    /**
     * An ArrayList of EdgeData containing (for now) all the data supplied.
     */
    private ArrayList<EdgeData> edges;

    /**
     * A HashMap that links a NodeData's KDV-number to the NodeData itself.
     *
     * This way we can get the specified NodeData from the EdgeDatas FNODE and
     * TNODE-fields.
     */
    private final HashMap<Integer, NodeData> nodeMap;

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
        edges = new ArrayList<>();

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
                edges.add(ed);
            }
        };

        // If your machine slows to a crawl doing inputting, try
        // uncommenting this. 
        // Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        // Invoke the loader class.
        loader.load(dir + "kdv_node_unload.txt",
                dir + "kdv_unload.txt");
        
        //edges = new KDTree(edgeList, nodeMap);
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
    }

    @Override
    public void paintComponent(Graphics g) {
        calculateFactor();
        for (EdgeData edge : edges) {
            if (edge.TYP != RoadType.FERRY.getTypeNumber()) {
                int fx = (int) ((nodeMap.get(edge.FNODE).X_COORD - lowX) / factor);
                int fy = getHeight() - (int)(( nodeMap.get(edge.FNODE).Y_COORD - lowY) / factor);
                int lx = (int) ((nodeMap.get(edge.TNODE).X_COORD - lowX) / factor);
                int ly = getHeight() - (int)(( nodeMap.get(edge.TNODE).Y_COORD - lowY) / factor);

                g.drawLine(fx, fy, lx, ly);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Map loader = new Map();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(loader);
        frame.getContentPane().addMouseListener(loader);
        frame.getContentPane().addMouseMotionListener(loader);
        frame.pack();
        frame.repaint();
        frame.setVisible(true);
    }
    
    private void zoomRect(double startX, double startY, double stopX, double stopY)
    {
        if (startX < stopX && startY < stopY) {
            throw new UnsupportedOperationException("Not yet implemented");
            //repaint();
        }
    }
    
    //Tracks exact position of mouse pointer
    private void trackMouse(double xTrack, double yTrack)
    {
        System.out.println("x:" + xTrack + "| y:" + yTrack);
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
        pressed = me;
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        released = me;
        
        zoomRect(pressed.getX(), pressed.getY(), released.getX(), released.getY());
        pressed = null;
        released = null;
    }



    @Override
    public void mouseExited(MouseEvent me) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        trackMouse(trackX = me.getX(), trackY = me.getY());
    }
}
