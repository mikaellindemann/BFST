
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class OSMViewer extends JComponent implements MouseMotionListener
{

    private static JLabel label;
    private final Map<RoadType, Set<Way>> waymap;
    private final Map<Long, Node> nodemap;

    private final double minX = 230000,
            minY = 82000,
            maxX = 245000,
            maxY = 89500;
    private double factor;
    private double xLeft, yTop, xRight, yBottom;

    public OSMViewer()
    {
        nodemap = new HashMap<>();

        Thread n = new Thread(new Loader(nodemap));
        n.start();

        for (RoadType rt : RoadType.values()) {
            rt.enable();
        }
        waymap = new HashMap<>();

        Thread ed = null;

        for (RoadType rt : RoadType.values()) {
            if (rt.isEnabled()) {
                waymap.put(rt, new HashSet<Way>());
                Thread e = new Thread(new Loader(rt, waymap));
                e.start();
                if (rt == RoadType.NOTAG) {
                    ed = e;
                }
            }
        }
        try {
            if (ed != null) {
                ed.join();
            }
            n.join();
        } catch (InterruptedException ex) {
            //Do nothing.
        }

        xLeft = minX;
        xRight = maxX;
        yTop = maxY;
        yBottom = minY;
    }

    public void setSmthng(double x1, double x2, double y1, double y2)
    {
        xLeft = x1;
        xRight = x2;
        yBottom = y1;
        yTop = y2;
    }

    private void calculateFactor()
    {
        factor = Math.abs(xRight - xLeft) / getWidth();
        if (Math.abs(yBottom - yTop) / getHeight() > factor) {
            factor = Math.abs(yBottom - yTop) / getHeight();
        }
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(1000, 670);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        System.out.println("paintComponent");
        calculateFactor();
        waytype:
        for (RoadType rt : RoadType.values()) {
            if (rt.isEnabled()) {
                for (Way way : waymap.get(rt)) {
                    Node fNode;
                    Node tNode = null;
                    for (long l : way.getNodeIds()) {
                        Node n = nodemap.get(l);
                        if (n == null) {
                            continue;
                        }
                        fNode = tNode;
                        tNode = n;

                        if (fNode == null) {
                            continue;
                        }
                        int tx = (int) ((tNode.getX() - xLeft) / factor);
                        int fx = (int) ((fNode.getX() - xLeft) / factor);
                        int ty = getHeight() - (int) ((tNode.getY() - yBottom) / factor);
                        int fy = getHeight() - (int) ((fNode.getY() - yBottom) / factor);
                        g.drawLine(fx, fy, tx, ty);
                    }
                }
            }
        }
    }

    public static void main(String[] args)
    {
        long t = System.currentTimeMillis();
        JFrame frame = new JFrame();
        label = new JLabel(" ");
        OSMViewer viewer = new OSMViewer();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(label, BorderLayout.SOUTH);
        frame.getContentPane().add(viewer);
        frame.getContentPane().addMouseMotionListener(viewer);
        frame.pack();
        frame.setVisible(true);
        System.out.println((System.currentTimeMillis() - t) / 1000 + " seconds");
        System.gc();
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {

    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        double x = minX + e.getX() * factor;
        double y = minY + (getHeight() - e.getY()) * factor;

        label.setText("(" + x + ", " + y + ")");
    }
}
