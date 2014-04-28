package dk.itu.groupe.loading;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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

    public LoadingPanel(int maxNodes, int maxEdges)
    {
        edges = 0;
        nodes = 0;
        this.maxNodes = maxNodes;
        this.maxEdges = maxEdges;
        setBackground(Color.BLACK);
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
        return new Dimension(380, 160);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        int promille = (int)((nodes / (double)maxNodes) * 300 + (edges / (double)maxEdges) * 700);
        if (promille < 300) {
            g.drawString("Loading nodes...", 10, 130);
        } else if (promille < 1000) {
            g.drawString("Loading edges...", 10, 130);
        } else {
            g.drawString("Building structures...", 10, 130);
        }
        g.drawRect(10, getHeight() - 20, getWidth() - 20, 10);
        g.fillRect(10, getHeight() - 20, (int)(promille / 1000.0 * (getWidth() - 20)), 10);
    }
}