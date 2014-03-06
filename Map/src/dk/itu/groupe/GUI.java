/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.groupe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 * @author Peter
 */
public class GUI extends JComponent
{

    JLabel roadName;
    private final JPanel remotePanel, remoteLayoutLeft, remoteLayoutRight;
    private final JFrame frame;
    private final Map map;
    private JButton buttonShowAll, buttonUp, buttonDown, buttonLeft, buttonRight, buttonZoomIn, buttonZoomOut;

    public GUI()
    {
        // Loads the entire map.
        map = new Map();
        
        // Creates buttons and their listeners.
        buttons();

        roadName = new JLabel("");

        // The rest of this constructor creates panels and sets layout for the
        // frame that is created at the bottom.
        remoteLayoutRight = new JPanel();
        remoteLayoutRight.setLayout(new BoxLayout(remoteLayoutRight, BoxLayout.Y_AXIS));
        remoteLayoutRight.setMinimumSize(new Dimension(120, 120));
        //remoteLayoutRight.setPreferredSize(new Dimension(100, 120));
        remoteLayoutRight.add(buttonDown);
        remoteLayoutRight.add(buttonRight);
        remoteLayoutRight.add(buttonZoomOut);

        remoteLayoutLeft = new JPanel();
        remoteLayoutLeft.setLayout(new BoxLayout(remoteLayoutLeft, BoxLayout.Y_AXIS));
        remoteLayoutLeft.setMinimumSize(new Dimension(120, 120));
        //remoteLayoutLeft.setPreferredSize(new Dimension(100, 150));
        remoteLayoutLeft.add(buttonUp);
        remoteLayoutLeft.add(buttonLeft);
        remoteLayoutLeft.add(buttonZoomIn);
        remoteLayoutLeft.add(buttonShowAll);
        remoteLayoutLeft.add(roadName);

        remotePanel = new JPanel(new GridLayout(0, 2));
        remotePanel.add(remoteLayoutLeft);
        remotePanel.add(remoteLayoutRight);

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(map, BorderLayout.CENTER);
        frame.getContentPane().add(remotePanel, BorderLayout.EAST);
        frame.getContentPane().addMouseListener(map);
        frame.getContentPane().addMouseMotionListener(map);
        frame.getContentPane().addMouseWheelListener(map);
        frame.pack();
        frame.repaint();
        frame.setVisible(true);
    }

    private void buttons()
    {
        buttonShowAll = new JButton("Show entire map");
        buttonShowAll.setMaximumSize(new Dimension(100, 40));
        buttonShowAll.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                map.reset();
            }
        });

        buttonUp = new JButton("Go up(↑)");
        buttonUp.setMaximumSize(new Dimension(100, 40));
        buttonUp.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "buttonUp");
        buttonUp.getActionMap().put("buttonUp", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                map.goUp();
            }
        });
        buttonUp.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                map.goUp();
            }
        });

        buttonRight = new JButton("Go right(→)");
        buttonRight.setMaximumSize(new Dimension(100, 40));
        buttonRight.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "buttonRight");
        buttonRight.getActionMap().put("buttonRight", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                map.goRight();
            }
        });
        buttonRight.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                map.goRight();
            }
        });

        buttonLeft = new JButton("Go left(←)");
        buttonLeft.setMaximumSize(new Dimension(100, 40));
        buttonLeft.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "buttonLeft");
        buttonLeft.getActionMap().put("buttonLeft", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                map.goLeft();
            }
        });
        buttonLeft.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                map.goLeft();
            }
        });

        buttonDown = new JButton("Go down(↓)");
        buttonDown.setMaximumSize(new Dimension(100, 40));
        buttonDown.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "buttonDown");
        buttonDown.getActionMap().put("buttonDown", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                map.goDown();
            }
        });
        buttonDown.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                map.goDown();
            }
        });

        buttonZoomIn = new JButton("Zoom in(+)");
        buttonZoomIn.setMaximumSize(new Dimension(100, 40));
        buttonZoomIn.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "buttonZoomIn");
        buttonZoomIn.getActionMap().put("buttonZoomIn", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                map.zoomIn();
            }
        });
        buttonZoomIn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                map.zoomIn();
            }
        });

        buttonZoomOut = new JButton("Zoom out(-)");
        buttonZoomOut.setMaximumSize(new Dimension(100, 40));
        buttonZoomOut.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "buttonZoomOut");
        buttonZoomOut.getActionMap().put("buttonZoomOut", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                map.zoomOut();
            }
        });
        buttonZoomOut.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                map.zoomOut();
            }
        });
    }
}
