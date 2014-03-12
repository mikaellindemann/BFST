package dk.itu.groupe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

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

        setLayout(new BorderLayout());
        add(flowPanel, BorderLayout.EAST);
        add(map, BorderLayout.CENTER);
    }

    /**
     * Creates buttons and assigns functions to buttons and keys.
     */
    private void buttons()
    {
        buttonShowAll = new JButton("Show entire map");
        buttonShowAll.setMaximumSize(new Dimension(100, 40));
        buttonShowAll.addActionListener(new Controller.Listener(model, Action.RESET));

        buttonUp = new JButton("↑");
        buttonUp.setMaximumSize(new Dimension(100, 40));
        buttonUp.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "buttonUp");
        Controller.Listener l = new Controller.Listener(model, Action.UP);
        buttonUp.getActionMap().put("buttonUp", l);
        buttonUp.addActionListener(l);

        buttonRight = new JButton("→");
        buttonRight.setMaximumSize(new Dimension(100, 40));
        buttonRight.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "buttonRight");
        l = new Controller.Listener(model, Action.RIGHT);
        buttonRight.getActionMap().put("buttonRight", l);
        buttonRight.addActionListener(l);

        buttonLeft = new JButton("←");
        buttonLeft.setMaximumSize(new Dimension(100, 40));
        buttonLeft.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "buttonLeft");
        l = new Controller.Listener(model, Action.LEFT);
        buttonLeft.getActionMap().put("buttonLeft", l);
        buttonLeft.addActionListener(l);

        buttonDown = new JButton("↓");
        buttonDown.setMaximumSize(new Dimension(100, 40));
        buttonDown.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "buttonDown");
        l = new Controller.Listener(model, Action.DOWN);
        buttonDown.getActionMap().put("buttonDown", l);
        buttonDown.addActionListener(l);

        buttonZoomIn = new JButton("+");
        buttonZoomIn.setMaximumSize(new Dimension(100, 40));
        buttonZoomIn.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "buttonZoomIn");
        l = new Controller.Listener(model, Action.ZOOM_IN);
        buttonZoomIn.getActionMap().put("buttonZoomIn", l);
        buttonZoomIn.addActionListener(l);

        buttonZoomOut = new JButton("-");
        buttonZoomOut.setMaximumSize(new Dimension(100, 40));
        buttonZoomOut.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "buttonZoomOut");
        l = new Controller.Listener(model, Action.ZOOM_OUT);
        buttonZoomOut.getActionMap().put("buttonZoomOut", l);
        buttonZoomOut.addActionListener(l);

        mouseZoom = new JRadioButton("Zoom", true);
        mouseZoom.addActionListener(new Controller.Listener(model, Action.MOUSE_ZOOM));

        mouseMove = new JRadioButton("Move", false);
        mouseMove.addActionListener(new Controller.Listener(model, Action.MOUSE_MOVE));

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
        if (arg.equals("updateRoadname")) {
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

                g.drawImage(image, 0, 0, getBackground(), null);
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
                model.calculateFactor();
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
                g.drawImage(image, 0, 0, null);
            }
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(model.getWidth(), model.getHeight());
        }
    }
}
