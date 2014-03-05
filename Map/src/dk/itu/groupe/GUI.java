/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.groupe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
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
public class GUI extends JComponent {
    
    JLabel label;
    private String yTrack;
    private String xTrack;
    
    public void gui() throws IOException {
        
        final Map loader = new Map();
        
        JPanel panel = new JPanel();
        JPanel remotePanel = new JPanel();
        JPanel remoteLayoutLeft = new JPanel();
        JPanel remoteLayoutRight = new JPanel();
        JFrame frame = new JFrame();
        
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.SOUTH);
        frame.add(remotePanel, BorderLayout.EAST);
        remoteLayoutLeft.setLayout(new BoxLayout(remoteLayoutLeft, BoxLayout.Y_AXIS));
        remoteLayoutRight.setLayout(new BoxLayout(remoteLayoutRight, BoxLayout.Y_AXIS));
        
        remoteLayoutLeft.setMinimumSize(new Dimension(100, 120));
        remoteLayoutLeft.setPreferredSize(new Dimension(100, 120));
        
        remoteLayoutRight.setMinimumSize(new Dimension(100, 120));
        remoteLayoutRight.setPreferredSize(new Dimension(100, 120));
        
        label = new JLabel("x:" + xTrack + "| y:" + yTrack);
        panel.add(label);
        
        remotePanel.add(remoteLayoutLeft);
        remotePanel.add(remoteLayoutRight);
        
        JButton button = new JButton("Show entire map");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                loader.reset();
            }
        });
        
        JButton buttonUp = new JButton("Go up(↑)");
        buttonUp.setMaximumSize(new Dimension(100, 40));

        //Add keybind function
        buttonUp.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "buttonUp");
        buttonUp.getActionMap().put("buttonUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loader.goUp();
            }
        });

        //Add button function
        buttonUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                loader.goUp();
            }
        });
        
        JButton buttonLeft = new JButton("Go left(←)");
        buttonLeft.setMaximumSize(new Dimension(100, 40));

        //Add keybind function
        buttonLeft.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "buttonLeft");
        buttonLeft.getActionMap().put("buttonLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loader.goLeft();
            }
        });

        //Add button function
        buttonLeft.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                loader.goLeft();
            }
        });
        
        JButton buttonRight = new JButton("Go right(→)");
        buttonRight.setMaximumSize(new Dimension(100, 40));

        //Add keybind function
        buttonRight.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "buttonRight");
        buttonRight.getActionMap().put("buttonRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loader.goRight();
            }
        });

        //Add button function
        buttonRight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                loader.goRight();
            }
        });
        
        JButton buttonDown = new JButton("Go down(↓)");
        buttonDown.setMaximumSize(new Dimension(100, 40));

        //Add keybind function
        buttonDown.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "buttonDown");
        buttonDown.getActionMap().put("buttonDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loader.goDown();
            }
        });

        //Add button function
        buttonDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                loader.goDown();
            }
        });
        
        JButton buttonZoomIn = new JButton("Zoom in(+)");
        buttonZoomIn.setMaximumSize(new Dimension(100, 40));

        //Add keybind function
        buttonZoomIn.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "buttonZoomIn");
        buttonZoomIn.getActionMap().put("buttonZoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loader.ZoomIn();
            }
        });

        //Add button function
        buttonZoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                loader.ZoomIn();
            }
        });
        
        JButton buttonZoomOut = new JButton("Zoom out(-)");
        buttonZoomOut.setMaximumSize(new Dimension(100, 40));

        //Add keybind function
        buttonZoomOut.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "buttonZoomOut");
        buttonZoomOut.getActionMap().put("buttonZoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loader.ZoomOut();
            }
        });

        //Add button function
        buttonZoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                loader.ZoomOut();
            }
        });
        panel.add(button);
        remoteLayoutLeft.add(buttonUp);
        remoteLayoutRight.add(buttonDown);
        remoteLayoutLeft.add(buttonLeft);
        remoteLayoutRight.add(buttonRight);
        remoteLayoutLeft.add(buttonZoomIn);
        remoteLayoutRight.add(buttonZoomOut);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(loader, BorderLayout.CENTER);
        frame.getContentPane().addMouseListener(loader);
        frame.getContentPane().addMouseMotionListener(loader);
        
        frame.getContentPane().add(remotePanel, BorderLayout.EAST);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        
        frame.pack();
        frame.repaint();
        frame.setVisible(true);
        
        
        
    }
}
