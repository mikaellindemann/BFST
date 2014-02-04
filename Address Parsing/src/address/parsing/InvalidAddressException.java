package address.parsing;

/**
 * An exception-class to tell that a string containing an address, could not be
 * parsed.
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class InvalidAddressException extends Exception
{
    /**
     * 
     * @param message A message containing additional information.
     */
    public InvalidAddressException(String message)
    {
        super(message);
    }
}
