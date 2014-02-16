package address.parsing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
*
* @author Mikael Lindemann Jepsen <mlin@itu.dk>
*/
public class AddressParsingTest {
    Connection con;

    public AddressParsingTest() {

    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    try {
            if (con == null || con.isValid(5)) {
                con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk/GroupE", "GroupE", "GroupE");
                // For local database
                // con = DriverManager.getConnection("jdbc:mysql://localhost:3306/GroupE", "root", "GroupE");

            }
        } catch (SQLException ex) {
            //Do something.
        }
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of parseAddress method, of class AddressParsing.
     * @throws java.lang.Exception
    */
    @Test
    public void testParseAddress() throws Exception {        
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM `roadnames`"); // WHERE `roadname`='Abrikosvej'
            ResultSet rs = ps.executeQuery();
                        
            int i = -1;
            while (rs.next()) {
                i++;
                String[] parsedAddress = AddressParsing.parseAddress(rs.getString(1));                 
                System.out.println(rs.getString(1) + " " + parsedAddress[0]);
                if (!rs.getString(1).equals(parsedAddress[0])) {
                    System.out.println(parsedAddress[0] + " line: " + (i+1) + " should have read: " + rs.getString(1));
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }                    
    }

}
