/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.groupe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 * @author Mikael
 */
public class Canvas extends JComponent
{

    private int x1, y1, x2, y2;
    private final int width, height;
    private final Map map;
    private final Color background;

    public Canvas(Map map, int width, int height, Color background)
    {
        this.background = background;
        this.map = map;
        this.width = width;
        this.height = height;
        setBackground(background);
        setOpaque(true);
    }

    public void setCoordinates(int x1, int y1, int x2, int y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public BufferedImage copyImage(BufferedImage source)
    {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(copyImage(map.getImage()), 0, 0, background, null);
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
