package address.parsing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class DatabaseConnection {
    Connection con;
    
    public DatabaseConnection()
    {
        connect();
    }

    /**
     * Creates a connection to the database.
     * 
     * If there is already a valid connection, nothing happens.
     */
    private void connect()
    {
        try {
            if (con == null || con.isValid(5)) {
                con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk/GroupE", "GroupE", "GroupE");
                // for local database
                // con = DriverManager.getConnection("jdbc:mysql://localhost:3306/GroupE", "root", "GroupE");
            }
        } catch (SQLException ex) {
            //Do something.
        }
    }

    /**
     * Uses regex to match an element in a table of the lookup-variable.
     *
     * @param lookup An enum containing information about the different tables
     * and rows.
     * @param name The roadname to match.
     * @return <pre>true</pre> if the element is found in the database,
     * <pre>false</pre> otherwise.
     */
    public boolean match(Lookup lookup, String name)
    {
        connect();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM `" + lookup.getTable() + "` WHERE `"
                    + lookup.getColumn() + "` REGEXP ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getString(lookup.getColumn()).equals(name)) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            // Do something.
            System.out.println(ex);
        }
        return false;
    }
}
