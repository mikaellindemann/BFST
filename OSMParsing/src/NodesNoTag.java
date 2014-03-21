
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class NodesNoTag
{

    public static void main(String[] args)
    {
        Out nodes = new Out("nodesNoTag.csv");
        In in = new In("nodes.osm");
        nodes.println("id, lat, lon");
        while (in.hasNextLine()) {
            String id = "";
            String lat = "";
            String lon = "";
            String s = in.readLine();

            Pattern p = Pattern.compile(" id=\".*?\"");
            Matcher m = p.matcher(s);
            if (m.find()) {
                String ss = m.group().replace("id=", "").replace("\"", "").trim();
                id = ss;
            }

            p = Pattern.compile(" lat=\".*?\"");
            m = p.matcher(s);
            if (m.find()) {
                String ss = m.group().replace("lat=", "").replace("\"", "").trim();
                lat = ss;
            }

            p = Pattern.compile(" lon=\".*?\"");
            m = p.matcher(s);
            if (m.find()) {
                String ss = m.group().replace("lon=", "").replace("\"", "").trim();
                lon = ss;
            }

            if (id.length() != 0 && lat.length() != 0 && lon.length() != 0) {
                nodes.println(
                        convertLatLon(
                                Long.parseLong(id), 
                                Double.parseDouble(lat), 
                                Double.parseDouble(lon)
                        )
                );
            }
        }
        nodes.close();
    }

    private static String convertLatLon(long id, double lat, double lon)
    {
        double latitude = lat;
        double longitude = lon;

        double mapWidth = 450000;

        // get x value
        double x = Math.round(((longitude + 180) * (mapWidth / 360)) * 10000000) / 10000000.0;

        // convert from degrees to radians
        double latRad = latitude * Math.PI / 180;

        // get y value
        double mercN = Math.log(Math.tan((Math.PI / 4) + (latRad / 2)));
        double y = Math.round((mapWidth * mercN / (2 * Math.PI)) * 10000000) / 10000000.0;
        
        return id + ", " + x + ", " + y;
    }
}
