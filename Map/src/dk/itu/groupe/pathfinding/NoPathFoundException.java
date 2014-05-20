package dk.itu.groupe.pathfinding;

/*
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) &amp;
 * Mikael Jepsen (mlin@itu.dk)
 */public class NoPathFoundException extends Exception
{
    public NoPathFoundException(String s) {
        super(s);
    }
}
