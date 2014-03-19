package dk.itu.groupe;

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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
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
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "buttonUp");
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "buttonRight");
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "buttonLeft");
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "buttonDown");
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "buttonZoomIn");
        view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "buttonZoomOut");
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
            model.zoomScrollIn(e.getX(), e.getY());
        } else {
            model.zoomScrollOut(e.getX(), e.getY());
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
        //Right click to reset.
        if (me.getButton() == 3) {
            model.reset();
            model.notifyObservers();
        } else {
            model.setPressed(me.getPoint());
            model.setDragged(me.getPoint());
        }
    }

    @Override
    public void mouseReleased(MouseEvent me)
    {
        if (me.getButton() == 1 && model.getMouseTool() == MouseTool.ZOOM) {
            if (model.getPressed().x == model.getDragged().x && model.getPressed().y == model.getDragged().y) {
                model.setPressed(null);
                model.setDragged(null);
                return;
            }
            model.zoomRect(model.getPressed().x, model.getPressed().y, model.getDragged().x, model.getDragged().y);
            model.setPressed(null);
            model.setDragged(null);
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
        if (model.getMouseTool() == MouseTool.MOVE) {
            if (model.getPressed() != null) {
                model.moveMap(model.getDragged().x - me.getX(), model.getDragged().y - me.getY());
            }
        }
        model.setDragged(me.getPoint());
        model.notifyObservers();
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
        JFrame frame = new JFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Model model = new Model();
        View view = new View(model);
        model.addObserver(view);
        Controller controller = new Controller(model, view);
        view.getMap().addComponentListener(controller);
        view.getMap().addMouseListener(controller);
        view.getMap().addMouseMotionListener(controller);
        view.getMap().addMouseWheelListener(controller);
        frame.addWindowStateListener(controller);
        frame.setContentPane(view);
        frame.pack();
        frame.setVisible(true);
        model.reset();
        model.notifyObservers();
    }
}
