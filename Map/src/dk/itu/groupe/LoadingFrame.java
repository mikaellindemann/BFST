/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.groupe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author Mikael
 */
public class LoadingFrame extends JComponent
{

    private final JFrame frame;

    private int edges;
    private int nodes;
    private final int maxEdges;
    private final int maxNodes;

    public LoadingFrame(int maxNodes, int maxEdges)
    {
        frame = new JFrame("Loading GroupE-Map");
        edges = 0;
        nodes = 0;
        this.maxNodes = maxNodes;
        this.maxEdges = maxEdges;
        frame.setBackground(Color.BLACK);
        setBackground(Color.BLACK);
        frame.setContentPane(this);
        frame.pack();
        frame.setVisible(true);
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
        g.setColor(Color.LIGHT_GRAY);
        int promille = (int)((nodes / (double)maxNodes) * 300 + (edges / (double)maxEdges) * 700);
        if (promille < 300) {
            g.drawString("Loading nodes", getWidth() / 4, getHeight() / 2);
        } else if (promille < 1000) {
            g.drawString("Loading edges", getWidth() / 4, getHeight() / 2);
        } else {
            g.drawString("Building structures", getWidth() / 4, getHeight() / 2);
        }
        g.drawRect(10, getHeight() - 20, getWidth() - 20, 10);
        g.fillRect(10, getHeight() - 20, (int)(promille / 1000.0 * (getWidth() - 20)), 10);
    }
    
    public void dispose()
    {
        frame.setVisible(false);
        frame.dispose();
    }
}
