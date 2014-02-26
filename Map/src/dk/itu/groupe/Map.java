package dk.itu.groupe;

import java.util.ArrayList;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.HashMap;

public class Map extends JComponent {

    private final ArrayList<NodeData> nodes;
    private final ArrayList<EdgeData> edges;
    private final HashMap<Integer, NodeData> nodeMap;
    private final int width = 1300, height = 700;

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        for (EdgeData edge : edges) {
            int fx = (int) nodeMap.get(edge.FNODE).X_COORD / 500 - 844;
            int fy = height - ((int) nodeMap.get(edge.FNODE).Y_COORD / 500 - 12098);
            int lx = (int) nodeMap.get(edge.TNODE).X_COORD / 500 - 844;
            int ly = height - ((int) nodeMap.get(edge.TNODE).Y_COORD / 500 - 12098);

            g.drawLine(fx, fy, lx, ly);
        }
    }

    public Map() throws IOException {
        String dir = "./data/";

		// For this example, we'll simply load the raw data into
        // ArrayLists.
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        nodeMap = new HashMap<>();

		// For that, we need to inherit from KrakLoader and override
        // processNode and processEdge. We do that with an 
        // anonymous class. 
        KrakLoader loader = new KrakLoader() {
            @Override
            public void
                    processNode(NodeData nd) {
                nodes.add(nd);
            }

            @Override
            public void
                    processEdge(EdgeData ed) {
                edges.add(ed);
            }
        };

		// If your machine slows to a crawl doing inputting, try
        // uncommenting this. 
        // Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        // Invoke the loader class.
        loader.load(dir + "kdv_node_unload.txt",
                dir + "kdv_unload.txt");

        for (NodeData nd : nodes) {
            nodeMap.put(nd.KDV, nd);
        }

        int xlow = Integer.MAX_VALUE, xhigh = Integer.MIN_VALUE;
        int ylow = Integer.MAX_VALUE, yhigh = Integer.MIN_VALUE;
        for (NodeData nd : nodes) {
            if (nd.X_COORD < xlow) {
                xlow = (int) nd.X_COORD;
            }
            if (nd.X_COORD > xhigh) {
                xhigh = (int) nd.X_COORD;
            }
            if (nd.Y_COORD < ylow) {
                ylow = (int) nd.Y_COORD;
            }
            if (nd.Y_COORD > yhigh) {
                yhigh = (int) nd.Y_COORD;
            }
        }

        System.out.println("Nodes:");
        System.out.println("Min: (" + xlow + ", " + ylow + ")");
        System.out.println("Max: (" + xhigh + ", " + yhigh + ")");
    }

    public static void main(String[] args) throws IOException {
        Map loader = new Map();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(loader);

        frame.pack();
        frame.repaint();
        frame.setVisible(true);
    }
}
