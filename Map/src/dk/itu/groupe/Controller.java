package dk.itu.groupe;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class Controller implements
        MouseListener,
        MouseMotionListener,
        MouseWheelListener,
        ComponentListener,
        WindowStateListener
{

    private final Model model;
    private final View view;

    public Controller(Model model, View view)
    {
        this.model = model;
        this.view = view;
        view.getActionMap().put(Action.UP, Action.UP.getListener(model));
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), Action.UP);
        view.getActionMap().put(Action.RIGHT, Action.RIGHT.getListener(model));
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), Action.RIGHT);
        view.getActionMap().put(Action.LEFT, Action.LEFT.getListener(model));
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), Action.LEFT);
        view.getActionMap().put(Action.DOWN, Action.DOWN.getListener(model));
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), Action.DOWN);
        view.getActionMap().put(Action.ZOOM_IN, Action.ZOOM_IN.getListener(model));
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), Action.ZOOM_IN);
        view.getActionMap().put(Action.ZOOM_OUT, Action.ZOOM_OUT.getListener(model));
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), Action.ZOOM_OUT);
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), Action.ZOOM_IN);
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), Action.ZOOM_OUT);
    }

    @Override
    public void mouseMoved(MouseEvent me)
    {
        model.updateRoadname(me.getX(), me.getY());
        model.notifyObservers("updateRoadname");
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (e.getWheelRotation() < 0) {
            model.zoomInScroll(e.getX(), e.getY());
        } else {
            model.zoomOutScroll(e.getX(), e.getY());
        }
        model.notifyObservers();
    }

    @Override
    public void mouseClicked(MouseEvent me)
    {
    }

    @Override
    public void mouseEntered(MouseEvent me)
    {

    }

    @Override
    public void mousePressed(MouseEvent me)
    {
        if (SwingUtilities.isLeftMouseButton(me)) {
            model.setPressed(me.getPoint());
            model.setDragged(me.getPoint());
        }
    }

    @Override
    public void mouseReleased(MouseEvent me)
    {
        if (SwingUtilities.isLeftMouseButton(me) && model.getMouseTool() == MouseTool.ZOOM) {
            if (model.getPressed().x != model.getDragged().x && model.getPressed().y != model.getDragged().y) {
                model.zoomRect(model.getPressed().x, model.getPressed().y, model.getDragged().x, model.getDragged().y);
                model.notifyObservers();
            }
            model.setPressed(null);
            model.setDragged(null);
        }
        //Right click to reset.
        if (SwingUtilities.isRightMouseButton(me)) {
            model.reset();
            model.notifyObservers();
        }
    }

    @Override
    public void mouseExited(MouseEvent me)
    {

    }

    @Override
    public void mouseDragged(MouseEvent me)
    {
        if (SwingUtilities.isLeftMouseButton(me)) {
            if (model.getMouseTool() == MouseTool.MOVE) {
                if (model.getPressed() != null) {
                    model.moveMap(model.getDragged().x - me.getX(), model.getDragged().y - me.getY());
                }
            }
            model.setDragged(me.getPoint());
            model.notifyObservers();
        }
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        model.setSize(view.getMap().getSize().width, view.getMap().getSize().height);
        model.notifyObservers();
    }

    @Override
    public void componentMoved(ComponentEvent e)
    {
    }

    @Override
    public void componentShown(ComponentEvent e)
    {
        componentMoved(e);
    }

    @Override
    public void componentHidden(ComponentEvent e)
    {

    }

    @Override
    public void windowStateChanged(WindowEvent e)
    {
        model.setSize(view.getMap().getSize().width, view.getMap().getSize().height);
        model.notifyObservers();
    }

    public static class Listener extends AbstractAction
    {

        private final Model model;
        private final Action action;

        public Listener(Model model, Action action)
        {
            this.model = model;
            this.action = action;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            switch (action) {
                case RESET:
                    model.reset();
                    break;
                case UP:
                    model.goUp(30);
                    break;
                case DOWN:
                    model.goDown(30);
                    break;
                case LEFT:
                    model.goLeft(30);
                    break;
                case RIGHT:
                    model.goRight(30);
                    break;
                case ZOOM_IN:
                    model.zoomIn();
                    break;
                case ZOOM_OUT:
                    model.zoomOut();
                    break;
                case MOUSE_MOVE:
                    model.setMouseTool(MouseTool.MOVE);
                    return;
                case MOUSE_ZOOM:
                    model.setMouseTool(MouseTool.ZOOM);
                    return;
            }
            model.notifyObservers();
        }
    }

    public static void main(String[] args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace(System.err);
        }
        String dataset = (String) JOptionPane.showInputDialog(null,
                "Do you want to use Krak data or OpenStreetMap-data?\n"
                + "Krak is a smaller and older dataset, but loads faster\n"
                + "OpenStreetMap is newer and contains more data.",
                "Choose data",
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Krak", "OpenStreetMap"},
                "OpenStreetMap");
        if (dataset == null) {
            return;
        }
        long time = System.currentTimeMillis();
        JFrame frame = new JFrame("GroupE-map Loading");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("res/Icon.png").getImage());
        Model model = new Model(dataset);
        frame.add(model.getLoadingPanel());
        frame.pack();
        frame.setVisible(true);
        model.load();
        frame.setVisible(false);
        frame.remove(model.getLoadingPanel());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        View view = new View(model);
        model.addObserver(view);
        Controller controller = new Controller(model, view);
        view.getMap().addComponentListener(controller);
        view.getMap().addMouseListener(controller);
        view.getMap().addMouseMotionListener(controller);
        view.getMap().addMouseWheelListener(controller);
        frame.addWindowStateListener(controller);
        JPanel glassPane = new JPanel(new BorderLayout());
        glassPane.setOpaque(false);
        frame.setGlassPane(glassPane);
        frame.add(view);
        frame.setTitle("GroupE-map");
        frame.pack();
        frame.setVisible(true);
        System.out.println((System.currentTimeMillis() - time) / 1000.0);
    }
}
