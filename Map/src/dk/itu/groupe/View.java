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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
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

    JLabel roadName;
    private BufferedImage image;
    private final JPanel remotePanel, keyPad, directionPanel;
    private final JComponent map;
    private JPanel flowPanel, leftPanel;
    private final Model model;
    private JButton buttonShowAll, buttonUp, buttonDown, buttonLeft, buttonRight, buttonZoomIn, buttonZoomOut, searchButton;
    private JLabel label_from, label_to;
    private JTextField textField_from, textField_to;
    private JRadioButton mouseMove, mouseZoom;
    private ButtonGroup mouse;
    private final Color BGColor = Color.decode("#457B85");
    private MouseListener e;
    boolean toggle_direction = false;
    private final String fontStandard = "calibri";
    private final int smallFontSize = 15;

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
        leftPanel.setPreferredSize(new Dimension(20,1));
        directionPanel.add(leftPanel);

        leftPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        leftPanel.addMouseListener(new MyMouseListener());

        flowPanel.setBackground(BGColor);
        remotePanel.setBackground(BGColor);
        leftPanel.setBackground(BGColor);
        directionPanel.setBackground(BGColor);
        flowPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
        flowPanel.add(remotePanel);

        leftPanel.setVisible(true);

        setLayout(new BorderLayout());
        add(flowPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);
        add(map, BorderLayout.CENTER);
    }
    private class MyMouseListener implements MouseListener {  
        @Override
        public void mouseEntered(MouseEvent e) {
            if (toggle_direction == false) {
                leftPanel.setPreferredSize(new Dimension(200,1));
                leftPanel.add(label_from);
                leftPanel.add(textField_from);
                leftPanel.add(label_to);
                leftPanel.add(textField_to);
                leftPanel.add(searchButton);
                
                leftPanel.setVisible(true);
                label_from.setVisible(true);
                textField_from.setVisible(true);
                label_to.setVisible(true);
                textField_to.setVisible(true);
                searchButton.setVisible(true);
                
                System.out.println("Direction toggled");
                //Resetting frame, (Any better approach is appreciated)
                setVisible(false);
                setVisible(true);
                toggle_direction = true;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //Nothing yet
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //Nothing yet
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //Nothing yet
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (toggle_direction == true) {
                leftPanel.setPreferredSize(new Dimension(20,1));
                label_from.setVisible(false);
                textField_from.setVisible(false);
                label_to.setVisible(false);
                textField_to.setVisible(false);
                searchButton.setVisible(false);
                
                System.out.println("Direction untoggled");
                //Resetting frame, (Any better approach is appreciated)
                setVisible(false);
                setVisible(true);
                toggle_direction = false;
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

        mouseZoom = new JRadioButton("Zoom");
        mouseZoom.setFont(new Font("calibri", Font.PLAIN, smallFontSize));
        mouseZoom.addActionListener(Action.MOUSE_ZOOM.getListener(model));
        if (model.getMouseTool() == MouseTool.ZOOM) {
            mouseZoom.setSelected(true);
        } else {
            mouseZoom.setSelected(false);
        }
        mouseZoom.setBackground(BGColor);

        mouseMove = new JRadioButton("Move");
        mouseMove.setFont(new Font("calibri", Font.PLAIN, smallFontSize));
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
        
        searchButton = new JButton("Search");
        searchButton.setMaximumSize(new Dimension(100, 40));
        searchButton.addMouseListener(new MyMouseListener());
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String search_from = textField_from.getText();
                String search_to = textField_to.getText();
                System.out.println("search_from = " + search_from);
                System.out.println("search_to = " + search_to);
            }
        });
    }
    private void createLabels()
    {
        label_from = new JLabel();
        label_from.setText("From:");
        label_from.setFont(new Font("calibri", Font.PLAIN, smallFontSize));
        
        label_to = new JLabel();
        label_to.setText("To:");
        label_to.setFont(new Font("calibri", Font.PLAIN, smallFontSize));
    }
    
    private void createTextField()
    {
        textField_from = new JTextField();
        textField_from.setPreferredSize(new Dimension(180,20));
        textField_from.addMouseListener(new MyMouseListener());
        
        textField_to = new JTextField();
        textField_to.setPreferredSize(new Dimension(180,20));
        textField_to.addMouseListener(new MyMouseListener());
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
                for (RoadType rt : RoadType.values()) {
                    if (rt.isEnabled(model.getFactor())) {
                        gB.setStroke(new BasicStroke(1));
                        switch (rt) {
                            case HIGHWAY:
                            case PROJ_HIGHWAY:
                            case HIGHWAY_EXIT:
                                if (15 / model.getFactor() > 1) {
                                    gB.setStroke(new BasicStroke((float) (15 / model.getFactor())));
                                }
                                gB.setColor(Color.RED);
                                break;
                            case EXPRESSWAY:
                            case PROJ_EXPRESSWAY:
                            case EXPRESSWAY_EXIT:
                                if (10 / model.getFactor() > 1) {
                                    gB.setStroke(new BasicStroke((float) (10 / model.getFactor()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                                }
                                gB.setColor(Color.ORANGE);
                                break;
                            case PRIMARY_ROUTE:
                            case PROJ_PRIMARY_ROUTE:
                            case PRIMARY_ROUTE_EXIT:
                                if (8 / model.getFactor() > 1) {
                                    gB.setStroke(new BasicStroke((float) (8 / model.getFactor()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                                }
                                gB.setColor(Color.YELLOW);
                                break;
                            case SECONDARY_ROUTE:
                            case ROAD:
                            case OTHER_ROAD:
                            case PROJ_SECONDARY_ROUTE:
                            case PROJ_ROAD:
                            case PROJ_OTHER_ROAD:
                            case SECOUNDARY_ROUTE_EXIT:
                                if (3 / model.getFactor() > 1) {
                                    gB.setStroke(new BasicStroke((float) (3 / model.getFactor()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
                                }
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
                                gB.setColor(Color.GREEN);
                                break;
                            case FERRY:
                                gB.setStroke(new BasicStroke(1,
                                        BasicStroke.CAP_BUTT,
                                        BasicStroke.JOIN_MITER,
                                        10, new float[]{10}, 0));
                                gB.setColor(Color.BLUE.darker());
                                break;
                            default:
                                //Containing: UNKNOWN(0) and ALSO_UNKNOWN(85)
                                gB.setColor(Color.BLACK);
                        }
                        for (Edge edge : model.getEdges(rt, topLeft.x, bottomRight.y, bottomRight.x, topLeft.y)) {
                            if (rt == RoadType.EXACT_LOCATION_UNKNOWN) {
                                gB.setColor(Color.BLACK);
                                int x = (int) ((edge.line.getX1() - topLeft.x) / model.getFactor());
                                int y = getHeight() - (int) ((edge.line.getY1() - bottomRight.y) / model.getFactor());
                                gB.drawString(edge.VEJNAVN, x, y);
                                continue;
                            }

                            int fx = (int) ((edge.line.getX1() - topLeft.x) / model.getFactor());
                            int fy = getHeight() - (int) ((edge.line.getY1() - bottomRight.y) / model.getFactor());
                            int lx = (int) ((edge.line.getX2() - topLeft.x) / model.getFactor());
                            int ly = getHeight() - (int) ((edge.line.getY2() - bottomRight.y) / model.getFactor());

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
