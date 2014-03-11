package dk.itu.groupe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class GUI extends JComponent
{

    JLabel roadName;
    private final JPanel remotePanel, keyPad;
    private JPanel flowPanel;
    private final JFrame frame;
    private final Map map;
    private JButton buttonShowAll, buttonUp, buttonDown, buttonLeft, buttonRight, buttonZoomIn, buttonZoomOut;
    private JRadioButton mouseMove, mouseZoom;
    private ButtonGroup mouse;

    public GUI()
    {
        // Loads the entire map.
        map = new Map();
        //map.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));

        // Creates buttons and their listeners.
        buttons();
        roadName = new JLabel(" ");
        keyPad = new JPanel(new GridLayout(0, 3));
        keyPad.add(buttonZoomIn);
        keyPad.add(buttonUp);
        keyPad.add(buttonZoomOut);
        keyPad.add(buttonLeft);
        keyPad.add(buttonDown);
        keyPad.add(buttonRight);

        remotePanel = new JPanel(new GridLayout(0, 1));
        flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowPanel.add(keyPad);
        remotePanel.add(flowPanel);
        flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowPanel.add(buttonShowAll);
        remotePanel.add(flowPanel);
        flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowPanel.add(roadName);
        remotePanel.add(flowPanel);
        flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowPanel.add(mouseZoom);
        flowPanel.add(mouseMove);
        remotePanel.add(flowPanel);

        flowPanel = new JPanel(new FlowLayout());
        flowPanel.setPreferredSize(new Dimension(320, 300));
        flowPanel.add(remotePanel);

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(flowPanel, BorderLayout.EAST);
        frame.getContentPane().add(map, BorderLayout.CENTER);
        frame.getContentPane().addMouseListener(map);
        frame.getContentPane().addMouseMotionListener(map);
        frame.getContentPane().addMouseWheelListener(map);
        frame.pack();
        frame.setGlassPane(new GlassPane(map));
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * Creates buttons and assigns functions to buttons and keys.
     */
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

        buttonUp = new JButton("↑");
        buttonUp.setMaximumSize(new Dimension(100, 40));
        buttonUp.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "buttonUp");
        buttonUp.getActionMap().put("buttonUp", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                map.goUp(30);
                map.repaint();
            }
        });
        buttonUp.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                map.goUp(30);
                map.repaint();
            }
        });

        buttonRight = new JButton("→");
        buttonRight.setMaximumSize(new Dimension(100, 40));
        buttonRight.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "buttonRight");
        buttonRight.getActionMap().put("buttonRight", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                map.goRight(30);
                map.repaint();
            }
        });
        buttonRight.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                map.goRight(30);
                map.repaint();
            }
        });

        buttonLeft = new JButton("←");
        buttonLeft.setMaximumSize(new Dimension(100, 40));
        buttonLeft.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "buttonLeft");
        buttonLeft.getActionMap().put("buttonLeft", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                map.goLeft(30);
                map.repaint();
            }
        });
        buttonLeft.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                map.goLeft(30);
                map.repaint();
            }
        });

        buttonDown = new JButton("↓");
        buttonDown.setMaximumSize(new Dimension(100, 40));
        buttonDown.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "buttonDown");
        buttonDown.getActionMap().put("buttonDown", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                map.goDown(30);
                map.repaint();
            }
        });
        buttonDown.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                map.goDown(30);
                map.repaint();
            }
        });

        buttonZoomIn = new JButton("+");
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

        buttonZoomOut = new JButton("-");
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
        
        mouseZoom = new JRadioButton("Zoom", true);
        mouseZoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                map.setMouse(MouseTool.ZOOM);
            }
        });
        
        mouseMove = new JRadioButton("Move", false);
        mouseMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map.setMouse(MouseTool.MOVE);
            }
        });
        
        mouse = new ButtonGroup();
        mouse.add(mouseZoom);
        mouse.add(mouseMove);
    }
}
