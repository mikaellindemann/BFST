package dk.itu.groupe;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class View extends JComponent implements Observer
{

    JLabel roadName;
    private final Map<Integer, BasicStroke> strokeMap;
    private BufferedImage image;
    private final JPanel remotePanel, keyPad;
    private final JComponent map;
    private JPanel flowPanel;
    private final Model model;
    private JButton buttonShowAll, buttonUp, buttonDown, buttonLeft, buttonRight, buttonZoomIn, buttonZoomOut;
    private JRadioButton mouseMove, mouseZoom;
    private ButtonGroup mouse;
    private final Color BGColor = Color.decode("#457B85");

    public View(final Model model)
    {
        strokeMap = new HashMap<>();
        for (int i = 0; i < 30; i++) {
            strokeMap.put(i, new BasicStroke(i, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        }
        strokeMap.put(-1, new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{10}, 0));

        this.model = model;
        map = new MapView();

        // Creates buttons and their listeners.
        createButtons();
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
    private void createButtons()
    {
        buttonShowAll = new JButton("Show entire map");
        buttonShowAll.setMaximumSize(new Dimension(100, 40));
        buttonShowAll.addActionListener(Action.RESET.getListener(model));

        buttonUp = new JButton("↑");
        buttonUp.setMaximumSize(new Dimension(100, 40));
        buttonUp.addActionListener(Action.UP.getListener(model));

        buttonRight = new JButton("→");
        buttonRight.setMaximumSize(new Dimension(100, 40));
        buttonRight.addActionListener(Action.RIGHT.getListener(model));

        buttonLeft = new JButton("←");
        buttonLeft.setMaximumSize(new Dimension(100, 40));
        buttonLeft.addActionListener(Action.LEFT.getListener(model));

        buttonDown = new JButton("↓");
        buttonDown.setMaximumSize(new Dimension(100, 40));
        buttonDown.addActionListener(Action.DOWN.getListener(model));

        buttonZoomIn = new JButton("+");
        buttonZoomIn.setMaximumSize(new Dimension(100, 40));
        buttonZoomIn.addActionListener(Action.ZOOM_IN.getListener(model));

        buttonZoomOut = new JButton("-");
        buttonZoomOut.setMaximumSize(new Dimension(100, 40));
        buttonZoomOut.addActionListener(Action.ZOOM_OUT.getListener(model));

        mouseZoom = new JRadioButton("Zoom");
        mouseZoom.addActionListener(Action.MOUSE_ZOOM.getListener(model));
        if (model.getMouseTool() == MouseTool.ZOOM) {
            mouseZoom.setSelected(true);
        } else {
            mouseZoom.setSelected(false);
        }
        mouseZoom.setBackground(BGColor);

        mouseMove = new JRadioButton("Move");
        mouseMove.addActionListener(Action.MOUSE_MOVE.getListener(model));
        if (model.getMouseTool() == MouseTool.MOVE) {
            mouseMove.setSelected(true);
        } else {
            mouseMove.setSelected(false);
        }
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
            if (model.getMouseTool() == MouseTool.ZOOM && model.getPressed() != null) {
                int x1 = model.getPressed().x;
                int y1 = model.getPressed().y;
                int x2 = model.getDragged().x;
                int y2 = model.getDragged().y;

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
                gB.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Point.Double topLeft = model.getLeftTop(), bottomRight = model.getRightBottom();
                for (CommonRoadType rt : CommonRoadType.values()) {
                    if (rt.isEnabled(model.getFactor())) {
                        double stroke;
                        gB.setStroke(strokeMap.get(1));
                        switch (rt) {
                            case MOTORWAY:
                            case MOTORWAY_LINK:
                                stroke = 15 / model.getFactor();
                                if (stroke > 1) {
                                    if (strokeMap.containsKey((int) stroke)) {
                                        gB.setStroke(strokeMap.get((int) stroke));
                                    } else {
                                        gB.setStroke(new BasicStroke((float) stroke));
                                    }
                                }
                                gB.setColor(Color.RED);
                                break;
                            case TRUNK:
                            case TRUNK_LINK:
                                stroke = 10 / model.getFactor();
                                if (stroke > 1) {
                                    if (strokeMap.containsKey((int) stroke)) {
                                        gB.setStroke(strokeMap.get((int) stroke));
                                    } else {
                                        gB.setStroke(new BasicStroke((float) stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                                    }
                                }
                                gB.setColor(Color.ORANGE);
                                break;
                            case PRIMARY:
                            case PRIMARY_LINK:
                                stroke = 8 / model.getFactor();
                                if (stroke > 1) {
                                    if (strokeMap.containsKey((int) stroke)) {
                                        gB.setStroke(strokeMap.get((int) stroke));
                                    } else {
                                        gB.setStroke(new BasicStroke((float) stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                                    }
                                }
                                gB.setColor(Color.YELLOW);
                                break;
                            case SECONDARY:
                            case TERTIARY:
                            case TERTIARY_LINK:
                            case ROAD:
                            case UNCLASSIFIED:
                            case SECONDARY_LINK:
                                stroke = 3 / model.getFactor();
                                if (stroke > 1) {
                                    if (strokeMap.containsKey((int) stroke)) {
                                        gB.setStroke(strokeMap.get((int) stroke));
                                    } else {
                                        gB.setStroke(new BasicStroke((float) (stroke), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                                    }
                                }
                                gB.setColor(Color.GRAY);
                                break;
                            case PATH:
                            case TRACK:
                                gB.setColor(Color.LIGHT_GRAY);
                                break;
                            case PEDESTRIAN:
                                gB.setColor(Color.BLUE);
                                break;
                            case TUNNEL:
                                gB.setColor(Color.GREEN);
                                break;
                            case FERRY:
                                gB.setStroke(strokeMap.get(-1));
                                gB.setColor(Color.BLUE.darker());
                                break;
                            default:
                                gB.setColor(Color.BLACK);
                        }
                        for (Edge edge : model.getEdges(rt, topLeft.x, bottomRight.y, bottomRight.x, topLeft.y)) {
                            if (rt == CommonRoadType.PLACES) {
                                gB.setColor(Color.BLACK);
                                int x = (int) ((edge.getLine().getX1() - topLeft.x) / model.getFactor());
                                int y = getHeight() - (int) ((edge.getLine().getY1() - bottomRight.y) / model.getFactor());
                                gB.drawString(edge.getRoadname(), x, y);
                                continue;
                            }

                            int fx = (int) ((edge.getLine().getX1() - topLeft.x) / model.getFactor());
                            int fy = getHeight() - (int) ((edge.getLine().getY1() - bottomRight.y) / model.getFactor());
                            int lx = (int) ((edge.getLine().getX2() - topLeft.x) / model.getFactor());
                            int ly = getHeight() - (int) ((edge.getLine().getY2() - bottomRight.y) / model.getFactor());

                            gB.drawLine(fx, fy, lx, ly);
                        }
                    }
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
