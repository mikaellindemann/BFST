package address.parsing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 *
 * @author Mikael Lindemann Jepsen <mlin@itu.dk>
 */
public class RoadNamesDB
{

    /*private static Connection con;
    private static Statement stmt;

    public static void main(String[] args)
    {

        Path file = Paths.get("road_names.txt");
        Charset charset = Charset.forName("ISO-8859-15");
        List<String> lines;

        try {
            lines = Files.readAllLines(file, charset);
            con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk/GroupE", "GroupE", "GroupE");
            stmt = con.createStatement();
            for (String address : lines) {
                stmt.executeUpdate("INSERT INTO `GroupE`.`roadnames` (`roadname`) VALUES ('" + address + "');");
            }
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }*/
}
