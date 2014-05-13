package dk.itu.groupe;

import dk.itu.groupe.data.CommonRoadType;
import dk.itu.groupe.data.Edge;
import dk.itu.groupe.data.Node;
import dk.itu.groupe.pathfinding.NoPathFoundException;
import dk.itu.groupe.util.LinkedList;
import dk.itu.groupe.util.Stack;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
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
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) &amp;
 * Mikael Jepsen (mlin@itu.dk)
 */
public class View extends JComponent implements Observer
{

    private static final DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
    private static final Font uiFont = new Font("calibri", Font.PLAIN, 15);

    private final Color BGColor = Color.decode("#457B85"), groundColor = Color.decode("#96FF70");
    private final JLabel roadName;
    private final JPanel leftPanelOpen, roadnamePanel;
    private final JComponent map;
    private final Model model;
    private final ImageIcon fromFlag = new ImageIcon("./res/flag_point_1.png"), toFlag = new ImageIcon("./res/flag_point_2.png");

    private JList<InternalEdge> routingList;
    private BufferedImage image;
    private JLabel label_path, label_distance, label_time;
    private JPopupMenu menu;

    public View(final Model model)
    {
        this.model = model;
        map = new MapView();

        // Creates buttons, labels and their listeners.
        createMenu();
        createTextField();
        createLabels();
        roadName = new JLabel(" ");

        roadnamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        roadnamePanel.setBackground(BGColor);
        roadnamePanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK));
        roadnamePanel.add(roadName);

        leftPanelOpen = new JPanel(new FlowLayout(FlowLayout.LEADING));
        leftPanelOpen.add(label_path);
        JScrollPane scrollPane = new JScrollPane(routingList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(230, 400));
        leftPanelOpen.add(scrollPane);
        leftPanelOpen.add(label_distance);
        leftPanelOpen.add(label_time);
        leftPanelOpen.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
        leftPanelOpen.setPreferredSize(new Dimension(250, map.getHeight()));
        leftPanelOpen.setBackground(BGColor);

        setLayout(new BorderLayout());
        add(roadnamePanel, BorderLayout.SOUTH);
        add(map, BorderLayout.CENTER);
    }

    public void addListSelectionListener(ListSelectionListener listener)
    {
        routingList.addListSelectionListener(listener);
    }

    private void createMenu()
    {
        menu = new JPopupMenu();
        menu.setLightWeightPopupEnabled(false);
        menu.updateUI();
        JMenuItem startPoint = new JMenuItem("Set startpoint", fromFlag);
        startPoint.addActionListener(Action.SET_FROM.getListener(model));
        JMenuItem endPoint = new JMenuItem("Set endpoint", toFlag);
        endPoint.addActionListener(Action.SET_TO.getListener(model));
        JMenuItem resetDirections = new JMenuItem("Reset directions");
        resetDirections.addActionListener(Action.RESET_DIRECTIONS.getListener(model));
        JMenuItem pathDist = new JRadioButtonMenuItem("Shortest path");
        pathDist.setSelected(!model.getPathByDriveTime());
        pathDist.addActionListener(Action.SHORTEST.getListener(model));
        JMenuItem pathTime = new JRadioButtonMenuItem("Fastest path");
        pathTime.setSelected(model.getPathByDriveTime());
        pathTime.addActionListener(Action.FASTEST.getListener(model));
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
        menu.show(map, e.x, e.y);
    }

    public void openLeftPanel()
    {
        JPanel glassPane = ((JPanel) ((JFrame) getTopLevelAncestor()).getGlassPane());
        glassPane.add(leftPanelOpen, BorderLayout.WEST);

        glassPane.setVisible(true);
    }

    public void closeLeftPanel()
    {
        JPanel glassPane = ((JPanel) ((JFrame) getTopLevelAncestor()).getGlassPane());
        glassPane.setVisible(false);
    }

    private void createLabels()
    {
        label_path = new JLabel("Path:");
        label_path.setFont(uiFont);
        label_path.setForeground(Color.WHITE);

        label_distance = new JLabel("Distance:");
        label_distance.setFont(uiFont);
        label_distance.setForeground(Color.WHITE);
        label_distance.setPreferredSize(new Dimension(180, 20));

        label_time = new JLabel("Time:");
        label_time.setFont(uiFont);
        label_time.setForeground(Color.WHITE);
        label_time.setPreferredSize(new Dimension(180, 20));
    }

    private void createTextField()
    {
        routingList = new JList<>();
        routingList.setFixedCellWidth(230);
    }

    public JComponent getMap()
    {
        return map;
    }

    public void updatePathList()
    {
        Stack<Edge> edges = null;
        try {
            if (model.pathPointsSet()) {
                edges = model.getPath();
            }
        } catch (NoPathFoundException ex) {
            showErrorMessage(ex.getMessage());
        }
        if (edges != null) {
            float totalLength = 0;
            float totalTime = 0;
            LinkedList<InternalEdge> routeStack = new LinkedList<>();
            String name = null;
            float length = 0;
            while (!edges.isEmpty()) {
                Edge edge = edges.pop();
                if (name == null) {
                    name = edge.getRoadname();
                    length += edge.getLength();
                    totalLength += edge.getLength();
                    totalTime += edge.getDriveTime();
                } else if (name.equals(edge.getRoadname())) {
                    length += edge.getLength();
                    totalLength += edge.getLength();
                    totalTime += edge.getDriveTime();
                } else {
                    routeStack.add(new InternalEdge(name, length));
                    name = edge.getRoadname();
                    length = edge.getLength();
                    totalLength += edge.getLength();
                    totalTime += edge.getDriveTime();
                }
                if (edges.isEmpty()) {
                    if (routeStack.isEmpty() || !routeStack.getLast().name.equals(edge.getRoadname())) {
                        routeStack.add(new InternalEdge(name, length));
                    }
                }
            }
            routingList.setListData(routeStack.toArray());
            label_distance.setText("Distance: " + df.format(totalLength / 1000) + " km");
            int hours = (int) totalTime / 60;
            totalTime -= hours * 60;
            int minutes = (int) totalTime;
            StringBuilder s = new StringBuilder("Time: ");
            switch (hours) {
                case 0:
                    break;
                case 1:
                    s.append("1 hour ");
                    break;
                default:
                    s.append(hours).append(" hours ");
            }
            switch (minutes) {
                case 0:
                    break;
                case 1:
                    s.append("1 minute");
                    break;
                default:
                    s.append(minutes).append(" minutes");
            }
            label_time.setText(s.toString());
        } else {
            routingList.setListData(new InternalEdge[0]);
            label_distance.setText("Distance: ");
            label_time.setText("Time: ");
        }
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if (arg != null) {
            if (arg.equals("updateRoadname")) {
                roadName.setText(model.getRoadname());
            } else if (arg.equals("updateRoadList")) {
                updatePathList();
                map.repaint();
            }
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
                    try {
                        Stack<Edge> edges = model.getPath();
                        gB.setColor(Color.BLUE);
                        gB.setStroke(new BasicStroke(5 * (float) model.getFactor(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        while (!edges.isEmpty()) {
                            Edge ed = edges.pop();
                            if (ed.getShape().intersects(topLeft.getX(), bottomRight.getY(), bottomRight.getX() - topLeft.getX(), topLeft.getY() - bottomRight.getY())) {
                                gB.draw(ed.getShape());
                            }
                        }
                    } catch (NoPathFoundException ex) {
                        showErrorMessage(ex.getMessage());
                    }
                }
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(image, 0, 0, null);
            }
            if (model.fromPoint() != null) {
                Node fromPoint = model.fromPoint();
                int x = (int) ((fromPoint.x() - model.getLeftTop().x) / model.getFactor()) - toFlag.getIconWidth();
                int y = getHeight() - (int) ((fromPoint.y() - model.getRightBottom().y) / model.getFactor()) - toFlag.getIconHeight();
                g.drawImage(fromFlag.getImage(), x, y, null);
            }
            if (model.toPoint() != null) {
                Node toPoint = model.toPoint();
                int x = (int) ((toPoint.x() - model.getLeftTop().x) / model.getFactor()) - toFlag.getIconWidth();
                int y = getHeight() - (int) ((toPoint.y() - model.getRightBottom().y) / model.getFactor()) - toFlag.getIconHeight();
                g.drawImage(toFlag.getImage(), x, y, null);
            }
            g.setColor(BGColor);
            g.fillRect(0, 0, 15, getHeight());
            g.setColor(Color.BLACK);
            g.drawLine(14, 0, 14, getHeight());
            g.drawLine(15, 0, 15, getHeight());
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
