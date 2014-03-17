package dk.itu.groupe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class View extends JComponent implements Observer
{

    JLabel roadName;
    private BufferedImage image;
    private final JPanel remotePanel, keyPad;
    private final JComponent map;
    private JPanel flowPanel;
    private final Model model;
    private JButton buttonShowAll, buttonUp, buttonDown, buttonLeft, buttonRight, buttonZoomIn, buttonZoomOut;
    private JRadioButton mouseMove, mouseZoom;
    private ButtonGroup mouse;
    private final Color BGColor = Color.decode("#457B85");
    
    //Change button color with gradient
    private void adjustGradient(Color color) {  
        List<Object> list = new ArrayList<>();  
        list.add(new Float(0.3F));  
        list.add(new Float(0));  
        list.add(color);  
        list.add(Color.WHITE);  
        list.add(color.darker().darker());  
        UIManager.put("Button.gradient", list);
        UIManager.put("RadioButton.gradient", list);
    }

    public View(final Model model)
    {
        this.model = model;
        map = new MapView();

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

        remotePanel = new JPanel(new FlowLayout());
        flowPanel = new JPanel(new FlowLayout());
        flowPanel.add(keyPad);
        flowPanel.setBackground(BGColor);
        remotePanel.add(flowPanel);
        flowPanel = new JPanel(new FlowLayout());
        flowPanel.add(buttonShowAll);
        flowPanel.setBackground(BGColor);
        remotePanel.add(flowPanel);
        flowPanel = new JPanel(new GridLayout(2, 0));
        flowPanel.add(mouseZoom);
        flowPanel.add(mouseMove);
        flowPanel.setBackground(BGColor);
        remotePanel.add(flowPanel);
        flowPanel = new JPanel(new FlowLayout());
        flowPanel.add(roadName);
        flowPanel.setBackground(BGColor);
        remotePanel.add(flowPanel);

        flowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        
        flowPanel.setBackground(BGColor);
        remotePanel.setBackground(BGColor);
        flowPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK));
        flowPanel.add(remotePanel);
        

        setLayout(new BorderLayout());
        add(flowPanel, BorderLayout.SOUTH);
        add(map, BorderLayout.CENTER);
    }

    /**
     * Creates buttons and assigns functions to buttons and keys.
     */
    private void buttons()
    {
        buttonShowAll = new JButton("Show entire map");
        buttonShowAll.setMaximumSize(new Dimension(100, 40));
        buttonShowAll.addActionListener(Action.RESET.getListener(model));
        adjustGradient(Color.ORANGE);

        buttonUp = new JButton("↑");
        buttonUp.setMaximumSize(new Dimension(100, 40));
        getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "buttonUp");
        getActionMap().put("buttonUp", Action.UP.getListener(model));
        buttonUp.addActionListener(Action.UP.getListener(model));

        buttonRight = new JButton("→");
        buttonRight.setMaximumSize(new Dimension(100, 40));
        getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "buttonRight");
        getActionMap().put("buttonRight", Action.RIGHT.getListener(model));
        buttonRight.addActionListener(Action.RIGHT.getListener(model));

        buttonLeft = new JButton("←");
        buttonLeft.setMaximumSize(new Dimension(100, 40));
        getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "buttonLeft");
        getActionMap().put("buttonLeft", Action.LEFT.getListener(model));
        buttonLeft.addActionListener(Action.LEFT.getListener(model));

        buttonDown = new JButton("↓");
        buttonDown.setMaximumSize(new Dimension(100, 40));
        getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "buttonDown");
        getActionMap().put("buttonDown", Action.DOWN.getListener(model));
        buttonDown.addActionListener(Action.DOWN.getListener(model));

        buttonZoomIn = new JButton("+");
        buttonZoomIn.setMaximumSize(new Dimension(100, 40));
        getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "buttonZoomIn");
        getActionMap().put("buttonZoomIn", Action.ZOOM_IN.getListener(model));
        buttonZoomIn.addActionListener(Action.ZOOM_IN.getListener(model));

        buttonZoomOut = new JButton("-");
        buttonZoomOut.setMaximumSize(new Dimension(100, 40));
        getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "buttonZoomOut");
        getActionMap().put("buttonZoomOut", Action.ZOOM_OUT.getListener(model));
        buttonZoomOut.addActionListener(Action.ZOOM_OUT.getListener(model));

        mouseZoom = new JRadioButton("Zoom", true);
        mouseZoom.addActionListener(Action.MOUSE_ZOOM.getListener(model));
        mouseZoom.setBackground(BGColor);

        mouseMove = new JRadioButton("Move", false);
        mouseMove.addActionListener(Action.MOUSE_MOVE.getListener(model));
        mouseMove.setBackground(BGColor);
        mouse = new ButtonGroup();
        mouse.add(mouseZoom);
        mouse.add(mouseMove);
    }

    public BufferedImage getImage()
    {
        return image;
    }

    public JComponent getMap()
    {
        return map;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if (arg != null && arg.equals("updateRoadname")) {
            roadName.setText(model.getRoadname());
        } else {
            map.repaint();
        }
    }

    private class MapView extends JComponent
    {
        @Override
        public void paintComponent(Graphics g)
        {
            if (model.getMouse() == MouseTool.ZOOM && model.getPressed() != null) {
                int x1 = model.getPressed().getX();
                int y1 = model.getPressed().getY();
                int x2 = model.getDragged().getX();
                int y2 = model.getDragged().getY();

                if (x1 > x2) {
                    int tmp = x2;
                    x2 = x1;
                    x1 = tmp;
                }
                if (y1 > y2) {
                    int tmp = y2;
                    y2 = y1;
                    y1 = tmp;
                }
                g.setClip(0, 0, getWidth(), getHeight());

                g.drawImage(image, 0, 0, Color.WHITE, null);
                g.setColor(Color.BLACK);

                double x = Math.abs(x2 - x1) / (double) getWidth();
                double y = Math.abs(y2 - y1) / (double) getHeight();

                if (x > y) {
                    int side = (int) ((x2 - x1) / ((double) getWidth() / getHeight()));
                    g.drawRect(x1, y1, x2 - x1, side);
                } else {
                    int side = (int) ((y2 - y1) * ((double) getWidth() / getHeight()));
                    g.drawRect(x1, y1, side, y2 - y1);
                }
            } else {
                image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D gB = image.createGraphics();
                //model.calculateFactor();
                Point.Double topLeft = model.getTopLeft(), bottomRight = model.getBottomRight();
                for (Edge edge : model.getEdges(topLeft.x, bottomRight.y, bottomRight.x, topLeft.y)) {
                    switch (edge.getType()) {
                        case HIGHWAY:
                        case PROJ_HIGHWAY:
                        case HIGHWAY_EXIT:
                            gB.setColor(Color.RED);
                            break;
                        case EXPRESSWAY:
                        case PROJ_EXPRESSWAY:
                        case EXPRESSWAY_EXIT:
                            gB.setColor(Color.GRAY);
                            break;
                        case PRIMARY_ROUTE:
                        case PROJ_PRIMARY_ROUTE:
                        case PRIMARY_ROUTE_EXIT:
                            gB.setColor(Color.YELLOW);
                            break;
                        case SECONDARY_ROUTE:
                        case ROAD:
                        case OTHER_ROAD:
                        case PROJ_SECONDARY_ROUTE:
                        case PROJ_ROAD:
                        case PROJ_OTHER_ROAD:
                        case SECOUNDARY_ROUTE_EXIT:
                        case OTHER_EXIT:
                            gB.setColor(Color.GRAY);
                            break;
                        case PATH:
                        case DIRT_ROAD:
                        case PROJ_PATH:
                            gB.setColor(Color.LIGHT_GRAY);
                            break;
                        case PEDESTRIAN_ZONE:
                            gB.setColor(Color.BLUE);
                            break;
                        case HIGHWAY_TUNNEL:
                        case EXPRESSWAY_TUNNEL:
                        case PRIMARY_ROUTE_TUNNEL:
                        case SECONDARY_ROUTE_TUNNEL:
                        case OTHER_ROAD_TUNNEL:
                        case SMALL_ROAD_TUNNEL:
                        case PATH_TUNNEL:
                            gB.setColor(Color.GREEN);
                            break;
                        case FERRY:
                            continue;
                        case EXACT_LOCATION_UNKNOWN:
                            continue;
                        default:
                            //Containing: UNKNOWN(0) and ALSO_UNKNOWN(85)
                            gB.setColor(Color.BLACK);
                    }

                    int fx = (int) ((edge.line.getX1() - topLeft.x) / model.getFactor());
                    int fy = getHeight() - (int) ((edge.line.getY1() - bottomRight.y) / model.getFactor());
                    int lx = (int) ((edge.line.getX2() - topLeft.x) / model.getFactor());
                    int ly = getHeight() - (int) ((edge.line.getY2() - bottomRight.y) / model.getFactor());

                    gB.drawLine(fx, fy, lx, ly);
                }
                g.drawImage(image, 0, 0, Color.WHITE, null);
            }
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(model.getWidth(), model.getHeight());
        }
    }
}
