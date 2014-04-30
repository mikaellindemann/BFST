package dk.itu.groupe;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class View extends JComponent implements Observer
{

    private final JLabel roadName;
    private final JPanel remotePanel, keyPad, directionPanel, leftPanelOpen;
    private final JComponent map;
    private final Model model;
    private final Color BGColor = Color.decode("#457B85");
    private final Color groundColor = Color.decode("#96FF70");

    private BufferedImage image;
    private JPanel flowPanel, leftPanel;
    private JButton buttonShowAll, buttonUp, buttonDown, buttonLeft, buttonRight, buttonZoomIn, buttonZoomOut, searchButton;
    private JLabel label_from, label_to;
    private JTextField textField_from, textField_to;
    private JRadioButton mousePath, mouseMove, mouseZoom;
    private ButtonGroup mouse;
    private static final Font uiFont = new Font("calibri", Font.PLAIN, 15);

    public View(final Model model)
    {
        this.model = model;
        map = new MapView();

        // Creates buttons, labels and their listeners.
        createButtons();
        createLabels();
        createTextField();
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
        flowPanel.add(mousePath);
        flowPanel.add(mouseZoom);
        flowPanel.add(mouseMove);
        flowPanel.setBackground(BGColor);
        remotePanel.add(flowPanel);
        flowPanel = new JPanel(new FlowLayout());
        flowPanel.add(roadName);
        flowPanel.setBackground(BGColor);
        remotePanel.add(flowPanel);

        flowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

        directionPanel = new JPanel(new FlowLayout());
        leftPanel = new JPanel(new FlowLayout());
        leftPanel.setPreferredSize(new Dimension(20, map.getHeight()));
        directionPanel.add(leftPanel);

        leftPanel = new JPanel();
        leftPanel.addMouseListener(new MyMouseListener());

        flowPanel.setBackground(BGColor);
        remotePanel.setBackground(BGColor);
        leftPanel.setBackground(BGColor);
        directionPanel.setBackground(BGColor);
        flowPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
        flowPanel.add(remotePanel);

        leftPanelOpen = new JPanel(new FlowLayout(FlowLayout.LEADING));
        leftPanelOpen.add(label_from);
        leftPanelOpen.add(textField_from);
        leftPanelOpen.add(label_to);
        leftPanelOpen.add(textField_to);
        leftPanelOpen.add(searchButton);
        leftPanelOpen.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
        leftPanelOpen.setPreferredSize(new Dimension(200, map.getHeight()));
        leftPanelOpen.setBackground(BGColor);

        setLayout(new BorderLayout());
        add(flowPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);
        add(map, BorderLayout.CENTER);
    }

    private class MyMouseListener implements MouseListener
    {

        JPanel empty;
        private boolean visible = false;

        @Override
        public void mouseEntered(MouseEvent e)
        {
            if (!visible) {
                if (empty == null) {
                    empty = new JPanel();
                    empty.setPreferredSize(flowPanel.getSize());
                }
                leftPanelOpen.addMouseListener(this);
                JPanel glassPane = ((JPanel) ((JFrame) getTopLevelAncestor()).getGlassPane());
                glassPane.add(leftPanelOpen, BorderLayout.WEST);
                add(empty, BorderLayout.SOUTH);
                glassPane.add(flowPanel, BorderLayout.SOUTH);
                glassPane.repaint();
                glassPane.setVisible(true);
                leftPanelOpen.repaint();
                revalidate();
                visible = true;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            //Nothing yet
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            //Nothing yet
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            //Nothing yet
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            if (visible && !leftPanelOpen.getBounds().contains(e.getPoint())) {
                JComponent glassPane = ((JComponent) ((JFrame) getTopLevelAncestor()).getGlassPane());
                glassPane.setVisible(false);
                add(leftPanel, BorderLayout.WEST);
                remove(empty);
                add(flowPanel, BorderLayout.SOUTH);
                leftPanel.repaint();
                revalidate();
                visible = false;
            }
        }
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

        mousePath = new JRadioButton("Path");
        mousePath.setFont(uiFont);
        mousePath.addActionListener(Action.MOUSE_PATH.getListener(model));
        if (model.getMouseTool() == MouseTool.PATH) {
            mousePath.setSelected(true);
        } else {
            mousePath.setSelected(false);
        }
        mousePath.setBackground(BGColor);

        mouseZoom = new JRadioButton("Zoom");
        mouseZoom.setFont(uiFont);
        mouseZoom.addActionListener(Action.MOUSE_ZOOM.getListener(model));
        if (model.getMouseTool() == MouseTool.ZOOM) {
            mouseZoom.setSelected(true);
        } else {
            mouseZoom.setSelected(false);
        }
        mouseZoom.setBackground(BGColor);

        mouseMove = new JRadioButton("Move");
        mouseMove.setFont(uiFont);
        mouseMove.addActionListener(Action.MOUSE_MOVE.getListener(model));
        if (model.getMouseTool() == MouseTool.MOVE) {
            mouseMove.setSelected(true);
        } else {
            mouseMove.setSelected(false);
        }
        mouseMove.setBackground(BGColor);
        mouse = new ButtonGroup();
        mouse.add(mousePath);
        mouse.add(mouseZoom);
        mouse.add(mouseMove);

        searchButton = new JButton("Search");
        searchButton.setMaximumSize(new Dimension(100, 40));
        searchButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                String search_from = textField_from.getText();
                String search_to = textField_to.getText();
                System.out.println("search_from = " + search_from);
                System.out.println("search_to = " + search_to);
            }
        });
    }

    private void createLabels()
    {
        label_from = new JLabel("From:");
        label_from.setFont(uiFont);
        label_from.setForeground(Color.WHITE);

        label_to = new JLabel("To:");
        label_to.setFont(uiFont);
        label_to.setForeground(Color.WHITE);
    }

    private void createTextField()
    {
        textField_from = new JTextField();
        textField_from.setPreferredSize(new Dimension(180, 20));

        textField_to = new JTextField();
        textField_to.setPreferredSize(new Dimension(180, 20));
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
            Point pressed = model.getPressed();
            if (model.getMouseTool() == MouseTool.ZOOM && model.getPressed() != null) {
                Point dragged = model.getDragged();
                int x1 = pressed.x;
                int y1 = pressed.y;
                int x2 = dragged.x;
                int y2 = dragged.y;

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
                image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

                Graphics2D gB = image.createGraphics();
                gB.setColor(Color.BLUE.darker().darker());
                gB.fillRect(0, 0, getWidth(), getHeight());
                gB.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                final double factor = model.getFactor();
                //gB.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                gB.setFont(gB.getFont().deriveFont(AffineTransform.getScaleInstance(factor, -factor)));
                Point.Double topLeft = model.getLeftTop(), bottomRight = model.getRightBottom();

                gB.scale(1 / factor, -1 / factor);
                gB.translate(-topLeft.x, -bottomRight.y - (getHeight() * factor));

                for (CommonRoadType rt : CommonRoadType.values()) {
                    if (rt.isEnabled(factor)) {
                        gB.setStroke(new BasicStroke(4));
                        switch (rt) {
                            case MOTORWAY:
                            case MOTORWAY_LINK:
                                gB.setStroke(new BasicStroke(15));
                                gB.setColor(Color.RED);
                                break;
                            case TRUNK:
                            case TRUNK_LINK:
                                gB.setStroke(new BasicStroke(10));
                                gB.setColor(Color.ORANGE);
                                break;
                            case PRIMARY:
                            case PRIMARY_LINK:
                                gB.setStroke(new BasicStroke(8));
                                gB.setColor(Color.YELLOW);
                                break;
                            case SECONDARY:
                            case TERTIARY:
                            case TERTIARY_LINK:
                            case ROAD:
                            case UNCLASSIFIED:
                            case SECONDARY_LINK:
                                gB.setStroke(new BasicStroke(3));
                                gB.setColor(Color.GRAY);
                                break;
                            case PATH:
                            case TRACK:
                                gB.setColor(Color.LIGHT_GRAY);
                                gB.setStroke(new BasicStroke(1));
                                break;
                            case PEDESTRIAN:
                                gB.setColor(Color.BLUE);
                                gB.setStroke(new BasicStroke(1));
                                break;
                            case TUNNEL:
                                gB.setColor(Color.GREEN);
                                break;
                            case FERRY:
                                gB.setStroke(new BasicStroke((float) factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{(float) (10 * factor)}, 0));
                                gB.setColor(Color.BLUE.darker());
                                break;
                            case COASTLINE:
                                gB.setColor(groundColor);
                                gB.setStroke(new BasicStroke(4));
                                break;
                            case RESIDENTIAL:
                                gB.setColor(Color.DARK_GRAY.darker());
                                gB.setStroke(new BasicStroke(2));
                                break;
                            case PLACES:
                                gB.setColor(Color.BLACK);
                                break;
                            default:
                                gB.setColor(Color.MAGENTA);
                        }
                        for (Object ed : model.getEdges(rt, topLeft.x, bottomRight.y, bottomRight.x, topLeft.y)) {
                            Edge edge = (Edge) ed;
                            if (edge.getShape().intersects(topLeft.x, bottomRight.y, bottomRight.x - topLeft.x, topLeft.y - bottomRight.y)) {
                                if (rt == CommonRoadType.PLACES) {
                                    Rectangle2D b = edge.getShape().getBounds2D();
                                    gB.drawString(edge.getRoadname(), (int) b.getCenterX(), (int) b.getCenterY());
                                    continue;
                                }
                                if (rt == CommonRoadType.COASTLINE) {
                                    gB.fill(edge.getShape());
                                } else {
                                    gB.draw(edge.getShape());
                                }
                            }
                        }
                    }
                }
                if (model.getMouseTool() == MouseTool.PATH && model.pathPointSet()) {
                    Iterable<Edge> edges = model.getPathTo(model.getMoved());
                    if (edges != null) {
                        gB.setColor(Color.RED);
                        gB.setStroke(new BasicStroke(6 * (float) model.getFactor()));
                        for (Edge ed : edges) {
                            gB.draw(ed.getShape());
                        }
                    }
                }

                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new TexturePaint(image, new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight())));
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(model.getWidth(), model.getHeight());
        }
    }
}
