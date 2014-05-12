package dk.itu.groupe;

/**
 * The Action enum is a way to have actions happen without writing listener-code
 * in the View-class.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public enum Action
{

    RESET, UP, DOWN, LEFT, RIGHT, ZOOM_IN, ZOOM_OUT, MOUSE_MOVE, MOUSE_ZOOM,
    SET_FROM, SET_TO, RESET_DIRECTIONS, SHORTEST, FASTEST;

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
