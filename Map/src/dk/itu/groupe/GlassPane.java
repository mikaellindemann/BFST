package dk.itu.groupe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JComponent;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class GlassPane extends JComponent
{

    private int x1, y1, x2, y2;
    private final int width, height;
    private final Map map;
    private final Color background;

    /**
     * Creates a new GlassPane.
     * 
     * @param map The map to draw on.
     */
    public GlassPane(Map map)
    {
        background = map.getBackground();
        this.map = map;
        width = map.getWidth();
        height = map.getHeight();
        setBackground(background);
        setOpaque(true);
    }

    /**
     * This meethod sets the coordinates of the rectangle that should be drawn
     * on repaint.
     *
     * @param x1 X-coordinate of the first point.
     * @param y1 Y-coordinate of the first point.
     * @param x2 X-coordinate of the second point.
     * @param y2 Y-coordinate of the second point.
     */
    public void setCoordinates(int x1, int y1, int x2, int y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        
        // These if-statements give weird behaviour, but this is actually what 
        // happens during zoom!
        if (x1 > x2) {
            this.x2 = x1;
            this.x1 = x2;
        }
        if (y1 > y2) {
            this.y2 = y1;
            this.y1 = y2;
        }
    }

    /**
     * This meethod sets the coordinates of the rectangle that should be drawn
     * on repaint.
     *
     * @param p1 First point.
     * @param p2 Second point.
     */
    public void setCoordinates(Point p1, Point p2)
    {
        setCoordinates(p1.x, p1.y, p2.x, p2.y);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(map.getImage(), 0, 0, background, null);
        g.setColor(Color.BLACK);

        double x = Math.abs(x2 - x1) / (double) width;
        double y = Math.abs(y2 - y1) / (double) height;

        if (x > y) {
            int side = (int) ((x2 - x1) / ((double) width / height));
            g.drawRect(x1, y1, x2 - x1, side);
        } else {
            int side = (int) ((y2 - y1) * ((double) width / height));
            g.drawRect(x1, y1, side, y2 - y1);
        }
    }
}
