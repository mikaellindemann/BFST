package address.parsing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class containing a static method to parse strings into address-arrays using
 * Regular Expressions.
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public class AddressParsing
{
    private static DatabaseConnection con;

    /**
     * Test cases.
     */
    private static final String[] addresses = new String[]{
        "Rued Langgaardsvej København",     //Should split, but doesn't
        "Rued Langgaardsvej København S",   //Should split, but doesn't
        "Rued Langgaards Vej 47",           // Tests that street number is 
        "Rued Langgaards Vej 47 ",          // recognized by the regex
        "H",                                // Should error
        "Rued Langgaards Vej 7B, 5. 2300 København S",
        "5. København S",
        "Rued Langgaards Vej 7B, 2300",
        "??????",                           // Should error
        "10. februar vej",
        "C. A. Nielsensvej 4, 3. sal 6543 København",
        "Siciliensgade 4, 3. tv. 2300 København S",
        "C Th. Zahles Vej 4, 1. mf. 1000 København",
        "Ndr. Gribsøvej 572A København",
        "København S",
        ", København",
        "Rued Langgaards Vej København S",
        "HAVEFORENING AF 1934",             // Currently unsupported addresses  
        "Haveforening af 1941",             // to show examples that do not work
        "Haveforeningen 515",               //
        "Haveforeningen af 10. maj 1918",   //
        "Haveforeningen af 1907",           //
        "Haveforeningen af 1918",           //
        "Haveforeningen af 1940",           //
        "Haveforeningen af 4. Juli 1917",   //
        "Haveforeningen af 4. Maj 1921"     //
    };

    /**
     * We don't need instances of this class.
     */
    private AddressParsing()
    {
    }

    /**
     * Takes a string containing an address and converts it into a string-array.
     *
     * @param address The address to parse. If there only is a street name
     * followed by a city name, it must be separated by a comma. We cannot
     * determine whether the string is a city or a street if there is only one
     * part of the string. Therefore it is assigned to the street part. We have
     * not considered street names containing numbers.
     * @return An array containing the separated address parts.
     * @throws InvalidAddressException If there is illegal characters (i.e. *+")
     * or if the address couldn't be parsed.
     */
    public static String[] parseAddress(String address) throws InvalidAddressException
    {
        if (con == null) {
            con = new DatabaseConnection();
        }
        // In Denmark we haven't seen cities or street names with less than two characters.
        if (address.length() < 2) {                 // Label 1
            throw new InvalidAddressException("The string is empty");
        }

        // Substitute non-usable information with separators.
        String a = address.replace(" sal ", ", ").replace(" i ", ", ").replace("  ", " ").trim();

        String streetName = "", number = "", buildingLetter = "", floor = "", postcode = "", city = "";

        // Matches any four digits which in Denmark is a postcode.
        Pattern postcodeP = Pattern.compile("\\d{4}");
        Matcher matcher = postcodeP.matcher(a);
        if (matcher.find()) {                       // Label 2
            postcode = matcher.group().trim();
            a = a.replace(postcode, ", ").trim();
        }

        // This matches a number which can be followed by a letter. I.e. 7 or 7A
        Pattern building = Pattern.compile("[0-9]+[a-zA-Z]{0,1}[\\s,]");
        
        // In scenarios where the street number is the last part of the string 
        // the regex would fail to recognize the pattern.
        a = a + " ";
        matcher = building.matcher(a);
        if (matcher.find()) {                       // Label 3
            // The next couple of lines separates a number from a letter if any
            // is present.
            number = matcher.group().replaceAll("\\D+", "");
            buildingLetter = matcher.group().replaceAll("[\\d,]+", "").trim();
            a = a.replace(matcher.group(), ", ").trim();
        }

        // This matches the street name from the beginning of the string (^).
        // It can be any number of non-punctuation characters not containing +,
        // numbers or space ([^\\p{P}\\+\\s0-9]) that may be followed by
        // space-characters (\\s{0,1}) (street names that has more than 1 word).
        Pattern street = Pattern.compile("^(([^\\p{P}\\+\\s0-9]|[0-9a-zA-Z\\.]+)+\\s{0,1})+");
        matcher = street.matcher(a);
        if (matcher.find()) {                       // Label 4
            if (con.match(Lookup.ROAD, matcher.group().trim())) {
                streetName = matcher.group().trim();
                a = a.replace(streetName, ", ").trim();
            }
        }

        // This matches the city name from the end of the string ($). See above
        // for regex syntax.
        Pattern cityP = Pattern.compile("([^\\p{P}\\+\\s0-9]+(\\s){0,1})+$");
        matcher = cityP.matcher(a);
        if (matcher.find()) {                       // Label 5
            city = matcher.group().trim();
            a = a.replace(city, ", ").trim();
        }

        a = a.replaceAll("\\s(tv|th|mf)[\\.]{0,1}\\s", ", ");

        // The floor is a number ([0-9]+) followed by dot (\\.)
        Pattern floorP = Pattern.compile("[0-9]+\\.");
        matcher = floorP.matcher(a);
        if (matcher.find()) {                       // Label 6
            // We don't want to save the dot.
            floor = matcher.group().replace(".", "").trim();
            a = a.replace(matcher.group(), ", ").trim();
        }

        // Remove all separating characters.
        a = a.replace(",", "").trim();

        // If anything is left, we have failed! This means that the address was
        // not written in a syntax that the method understands, or that it
        // contains illegal characters.
        if (a.length() != 0) {                      // Label 7
            //System.out.println("WTF");
            throw new InvalidAddressException(a);
        }

        return new String[]{streetName, number, buildingLetter, floor, postcode, city};
    }

    /**
     * Test-client for the method parseAddress(String).
     *
     * @param args Not used.
     */
    public static void main(String[] args)
    {
        String[][] parsedAddresses = new String[addresses.length][];
        // addresses.length corresponds to the number of test-cases.
        for (int i = 0; i < addresses.length; ++i) {
            try {
                parsedAddresses[i] = parseAddress(addresses[i]);
            } catch (InvalidAddressException ex) {
                parsedAddresses[i] = null;
            }
        }
                    
        Path file = Paths.get("AddressParsingTest.txt");
        Charset charset = Charset.defaultCharset();

        String[] printArray = new String[parsedAddresses.length];

        for (int i = 0; i < parsedAddresses.length; ++i) {
            String print = "";
            if (parsedAddresses[i] == null) {
                printArray[i] = "MALFORMED ADDRESS";
                System.out.println("MALFORMED ADDRESS");
                // Take the next array.
                continue;
            }
            for (String string : parsedAddresses[i]) {
                print += string + "#";
            }
            printArray[i] = print.substring(0, print.length() - 1);
            System.out.println(print.substring(0, print.length() - 1));
        }
        try {
            Files.write(file, Arrays.asList(printArray), charset, StandardOpenOption.CREATE);
        } catch (IOException ex) {
            System.err.println("Cannot write to file: " + file.toString());
            System.err.println(ex);
        }
    }
}