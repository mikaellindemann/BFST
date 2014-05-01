package dk.itu.groupe.loading;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class LoadingPanel extends JComponent
{

    private int edges;
    private int nodes;
    private final int maxEdges;
    private final int maxNodes;
    private BufferedImage image;

    public LoadingPanel(int maxNodes, int maxEdges)
    {
        edges = 0;
        nodes = 0;
        this.maxNodes = maxNodes;
        this.maxEdges = maxEdges;
        try {
            image = ImageIO.read(new File("res/Loading.png"));
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void countEdge()
    {
        edges++;
        if (edges % (maxEdges / 1000) == 0 || edges == maxEdges) {
            repaint();
        }
    }

    public void countNode()
    {
        nodes++;
        if (nodes % (maxNodes / 1000) == 0 || nodes == maxNodes) {
            repaint();
        }
    }
    
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(480, 288);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(image, 0, 0, null);
        int promille = (int)((nodes / (double)maxNodes) * 300 + (edges / (double)maxEdges) * 700);
        
        Color gColor = new Color(0x5B9EAA);
        g.setColor(gColor);
        if (promille < 300) {
            g.drawString("Loading nodes...", 10, 260);
        } else if (promille < 1000) {
            g.drawString("Loading edges...", 10, 260);
        } else {
            g.drawString("Building structures...", 10, 260);
        }
        g.drawRect(10, getHeight() - 20, getWidth() - 20, 10);
        g.fillRect(10, getHeight() - 20, (int)(promille / 1000.0 * (getWidth() - 20)), 10);
    }
}