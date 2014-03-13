package dk.itu.groupe;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public enum Action
{

    RESET, UP, DOWN, LEFT, RIGHT, ZOOM_IN, ZOOM_OUT, MOUSE_MOVE, MOUSE_ZOOM;

    private Model model;
    private Controller.Listener listener;

    public Controller.Listener getListener(Model model)
    {
        if (model == this.model) {
            return listener;
        }
        this.model = model;
        return listener = new Controller.Listener(model, this);
    }
}
