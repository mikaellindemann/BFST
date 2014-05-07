package dk.itu.groupe;

import dk.itu.groupe.data.CommonRoadType;
import dk.itu.groupe.data.Edge;
import dk.itu.groupe.data.Node;
import dk.itu.groupe.pathfinding.NoPathFoundException;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class View extends JComponent implements Observer
{

    private static final DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
    private static final Font uiFont = new Font("calibri", Font.PLAIN, 15);

    private final Color BGColor = Color.decode("#457B85"), groundColor = Color.decode("#96FF70");
    private final JLabel roadName;
    private final JPanel remotePanel, keyPad, directionPanel, leftPanelOpen;
    private final JComponent map;
    private final Model model;
    private final ImageIcon fromFlag = new ImageIcon("./res/flag_point_1.png"), toFlag = new ImageIcon("./res/flag_point_2.png");

    private JList<InternalEdge> routingList;
    private BufferedImage image;
    private JPanel flowPanel, leftPanel;
    private JButton buttonShowAll, buttonUp, buttonDown, buttonLeft, buttonRight, buttonZoomIn, buttonZoomOut;
    private JLabel label_path;
    private JPopupMenu menu;
    private Point e;

    public View(final Model model)
    {
        this.model = model;
        map = new MapView();

        // Creates buttons, labels and their listeners.
        createButtons();
        createMenu();
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
        leftPanelOpen.add(label_path);
        JScrollPane scrollPane = new JScrollPane(routingList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(180, 400));
        leftPanelOpen.add(scrollPane);
        leftPanelOpen.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
        leftPanelOpen.setPreferredSize(new Dimension(200, map.getHeight()));
        leftPanelOpen.setBackground(BGColor);

        setLayout(new BorderLayout());
        add(flowPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);
        add(map, BorderLayout.CENTER);
    }

    public void addListSelectionListener(ListSelectionListener listener)
    {
        routingList.addListSelectionListener(listener);
    }

    private void createMenu()
    {
        menu = new JPopupMenu();
        menu.setLightWeightPopupEnabled(true);
        menu.updateUI();
        JMenuItem startPoint = new JMenuItem("Set startpoint", fromFlag);
        startPoint.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                try {
                    model.setFromNode(model.translatePoint(e.x, e.y));
                    map.repaint();
                } catch (NoPathFoundException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    model.resetPointSet();
                }
            }

        });
        JMenuItem endPoint = new JMenuItem("Set endpoint", toFlag);
        endPoint.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                try {
                    model.setToNode(model.translatePoint(e.x, e.y));
                    map.repaint();
                } catch (NoPathFoundException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }

        });
        JMenuItem resetDirections = new JMenuItem("Reset directions");
        resetDirections.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                model.resetPointSet();
                map.repaint();
            }

        });
        JMenuItem pathDist = new JRadioButtonMenuItem("Shortest path");
        pathDist.setSelected(!model.getPathByDriveTime());
        pathDist.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                model.setPathByDriveTime(false);
                if (model.pathPointsSet()) {
                    map.repaint();
                }
            }

        });
        JMenuItem pathTime = new JRadioButtonMenuItem("Fastest path");
        pathTime.setSelected(model.getPathByDriveTime());
        pathTime.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                model.setPathByDriveTime(true);
                if (model.pathPointsSet()) {
                    map.repaint();
                }
            }
        });
        ButtonGroup paths = new ButtonGroup();
        paths.add(pathDist);
        paths.add(pathTime);

        JMenuItem mouseMove = new JRadioButtonMenuItem("Move");
        mouseMove.addActionListener(Action.MOUSE_MOVE.getListener(model));
        JMenuItem mouseZoom = new JRadioButtonMenuItem("Zoom");
        mouseZoom.addActionListener(Action.MOUSE_ZOOM.getListener(model));
        mouseMove.setSelected(model.getMouseTool() == MouseTool.MOVE);
        mouseZoom.setSelected(model.getMouseTool() == MouseTool.ZOOM);
        ButtonGroup group = new ButtonGroup();
        group.add(mouseMove);
        group.add(mouseZoom);

        JMenuItem reset = new JMenuItem("Show Denmark");
        reset.addActionListener(Action.RESET.getListener(model));

        menu.add(pathDist);
        menu.add(pathTime);
        menu.addSeparator();
        menu.add(startPoint);
        menu.add(endPoint);
        menu.addSeparator();
        menu.add(resetDirections);
        menu.addSeparator();
        menu.add(mouseMove);
        menu.add(mouseZoom);
        menu.addSeparator();
        menu.add(reset);
    }

    public void showContextMenu(Point e)
    {
        this.e = e;
        menu.show(map, e.x, e.y);
    }

    private class MyMouseListener extends MouseAdapter
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
    }

    private void createLabels()
    {
        label_path = new JLabel("Path:");
        label_path.setFont(uiFont);
        label_path.setForeground(Color.WHITE);
    }

    private void createTextField()
    {
        routingList = new JList<>();
        routingList.setFixedCellWidth(180);
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

    public void showErrorMessage(String message)
    {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private class MapView extends JComponent
    {

        @Override
        public void paintComponent(Graphics g)
        {
            if (image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
                image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            }
            Point2D pressed = model.getPressed();
            if (model.getMouseTool() == MouseTool.ZOOM && pressed != null) {
                Graphics2D gB = (Graphics2D) g;
                //gB.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                gB.drawImage(image, 0, 0, Color.BLUE.darker().darker(), null);

                double factor = model.getFactor();
                Point2D topLeft = model.getLeftTop(), bottomRight = model.getRightBottom();
                gB.scale(1 / factor, -1 / factor);
                gB.translate(-topLeft.getX(), -bottomRight.getY() - (getHeight() * factor));
                Point2D dragged = model.getDragged();
                double x1 = pressed.getX();
                double y1 = pressed.getY();
                double x2 = dragged.getX();
                double y2 = dragged.getY();

                if (x1 > x2) {
                    double tmp = x2;
                    x2 = x1;
                    x1 = tmp;
                }
                if (y1 > y2) {
                    double tmp = y2;
                    y2 = y1;
                    y1 = tmp;
                }
                gB.setColor(new Color(0, 0, 0, 150));

                double x = Math.abs(x2 - x1) / (double) getWidth();
                double y = Math.abs(y2 - y1) / (double) getHeight();

                if (x > y) {
                    int side = (int) ((x2 - x1) / ((double) getWidth() / getHeight()));
                    gB.fillRect((int) x1, (int) y1, (int) (x2 - x1), side);
                    gB.drawRect((int) x1, (int) y1, (int) (x2 - x1), side);
                } else {
                    int side = (int) ((y2 - y1) * ((double) getWidth() / getHeight()));
                    gB.fillRect((int) x1, (int) y1, side, (int) (y2 - y1));
                    gB.drawRect((int) x1, (int) y1, side, (int) (y2 - y1));
                }
            } else {
                Graphics2D gB = image.createGraphics();
                gB.setColor(Color.BLUE.darker().darker());
                gB.fillRect(0, 0, getWidth(), getHeight());
                final double factor = model.getFactor();
                gB.setFont(gB.getFont().deriveFont(AffineTransform.getScaleInstance(factor, -factor)));
                Point2D topLeft = model.getLeftTop(), bottomRight = model.getRightBottom();

                gB.scale(1 / factor, -1 / factor);
                gB.translate(-topLeft.getX(), -bottomRight.getY() - (getHeight() * factor));

                for (CommonRoadType rt : CommonRoadType.values()) {
                    if (rt.isEnabled(factor)) {
                        gB.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        gB.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        switch (rt) {
                            case MOTORWAY:
                            case MOTORWAY_LINK:
                                gB.setStroke(new BasicStroke(15, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                gB.setColor(Color.RED);
                                break;
                            case TRUNK:
                            case TRUNK_LINK:
                                gB.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                gB.setColor(Color.ORANGE);
                                break;
                            case PRIMARY:
                            case PRIMARY_LINK:
                                gB.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                gB.setColor(Color.YELLOW);
                                break;
                            case SECONDARY:
                            case TERTIARY:
                            case TERTIARY_LINK:
                            case ROAD:
                            case UNCLASSIFIED:
                            case SECONDARY_LINK:
                                gB.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                gB.setColor(Color.DARK_GRAY);
                                break;
                            case PATH:
                            case TRACK:
                                gB.setColor(Color.GRAY);
                                gB.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                break;
                            case PEDESTRIAN:
                                gB.setColor(Color.BLUE);
                                gB.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                break;
                            case TUNNEL:
                                gB.setColor(Color.GREEN);
                                break;
                            case FERRY:
                                gB.setStroke(new BasicStroke((float) factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{(float) (10 * factor)}, 0));
                                gB.setColor(Color.BLUE.darker());
                                break;
                            case COASTLINE:
                                gB.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                                gB.setColor(groundColor);
                                gB.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                break;
                            case RESIDENTIAL:
                                gB.setColor(Color.DARK_GRAY.darker());
                                gB.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                break;
                            case PLACES:
                                gB.setColor(Color.BLACK);
                                break;
                            default:
                                gB.setColor(Color.MAGENTA);
                        }
                        for (Object ed : model.getEdges(rt, topLeft.getX(), bottomRight.getY(), bottomRight.getX(), topLeft.getY())) {
                            Edge edge = (Edge) ed;
                            if (edge.getShape().intersects(topLeft.getX(), bottomRight.getY(), bottomRight.getX() - topLeft.getX(), topLeft.getY() - bottomRight.getY())) {
                                if (rt == CommonRoadType.PLACES) {
                                    Rectangle2D b = edge.getShape().getBounds2D();
                                    gB.drawString(edge.getRoadname(), (int) b.getCenterX(), (int) b.getCenterY());
                                    continue;
                                }
                                if (rt == CommonRoadType.COASTLINE) {
                                    gB.draw(edge.getShape());
                                    gB.fill(edge.getShape());
                                } else {
                                    gB.draw(edge.getShape());
                                }
                            }
                        }
                    }
                }
                if (model.pathPointsSet()) {
                    Deque<Edge> edges = null;
                    try {
                        edges = model.getPath();
                    } catch (NoPathFoundException ex) {
                        showErrorMessage(ex.getMessage());
                    }
                    if (edges != null) {
                        Deque<InternalEdge> routeStack = new ArrayDeque<>();
                        String name = null;
                        float length = 0;
                        for (Edge e : edges) {
                            if (name == null) {
                                name = e.getRoadname();
                                length += e.getLength();
                            } else if (name.equals(e.getRoadname())) {
                                length += e.getLength();
                            } else {
                                routeStack.add(new InternalEdge(name, length));
                                name = e.getRoadname();
                                length = e.getLength();
                            }
                        }
                        InternalEdge[] list = new InternalEdge[routeStack.size()];
                        for (int i = 0; !routeStack.isEmpty(); i++) {
                            list[i] = routeStack.pop();
                        }
                        routingList.setListData(list);
                        gB.setColor(Color.BLUE);
                        gB.setStroke(new BasicStroke(5 * (float) model.getFactor(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        for (Edge ed : edges) {
                            gB.draw(ed.getShape());
                        }
                    }
                }
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                image.flush();
                g2.drawImage(image, 0, 0, Color.BLUE.darker().darker(), null);
                if (model.fromPoint() != null) {
                    Node fromPoint = model.fromPoint();
                    int x = (int) ((fromPoint.x() - model.getLeftTop().x) / model.getFactor()) - toFlag.getIconWidth();
                    int y = getHeight() - (int) ((fromPoint.y() - model.getRightBottom().y) / model.getFactor()) - toFlag.getIconHeight();
                    g2.drawImage(fromFlag.getImage(), x, y, null);
                }
                if (model.toPoint() != null) {
                    Node toPoint = model.toPoint();
                    int x = (int) ((toPoint.x() - model.getLeftTop().x) / model.getFactor()) - toFlag.getIconWidth();
                    int y = getHeight() - (int) ((toPoint.y() - model.getRightBottom().y) / model.getFactor()) - toFlag.getIconHeight();
                    g2.drawImage(toFlag.getImage(), x, y, null);
                }
            }
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(model.getWidth(), model.getHeight());
        }
    }

    class InternalEdge
    {

        float length;
        String name;
        InternalEdge(String roadname, float length)
        {
            this.name = roadname;
            this.length = length;
        }

        @Override
        public String toString()
        {
            return name + " " + df.format(length / 1000) + " km";
        }
    }
}
