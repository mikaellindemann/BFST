
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OSM
{

    public static void main(String[] args)
    {
        boolean way = false, waySection = false;
        boolean node = false, nodeSection = false;
        Out ways = new Out("ways.csv");
        Out nodes = new Out("nodes.csv");
        In osm = new In("denmark-latest.osm");
        List<String> nodeList = new ArrayList<>();
        List<String> wayList = new ArrayList<>();
        while (osm.hasNextLine()) {
            String s = osm.readLine();

            s = s.replaceFirst(" timestamp=\".*?\"", "");
            s = s.replaceFirst(" user=\".*?\"", "");
            s = s.replaceFirst(" version=\".*?\"", "");
            s = s.replaceFirst(" changeset=\".*?\"", "");
            s = s.replaceFirst(" uid=\".*?\"", "").trim();

            if (s.matches(".*?\\<way.+?\\/\\>.*?")) {
                wayList.add(s);
                way = false;
                waySection = true;
            } else if (s.matches(".*?\\<\\/way\\>.*?")) {
                wayList.add(s);
                way = false;
                waySection = true;
            } else if (s.matches(".*?\\<way.+?\\>.*?")) {
                wayList.add(s);
                way = true;
            } else if (way) {
                wayList.add(s);
            }
            if (waySection) {
                ways.println(translateEdge(wayList));
                waySection = false;
                wayList.clear();
            }

            if (s.matches(".*?\\<node.+?\\/\\>.*?")) {
                nodeList.add(s);
                node = false;
                nodeSection = true;
            } else if (s.matches(".*?\\<\\/node\\>.*?")) {
                nodeList.add(s);
                node = false;
                nodeSection = true;
            } else if (s.matches(".*?\\<node.+?\\>.*?")) {
                nodeList.add(s);
                node = true;
            } else if (node) {
                nodeList.add(s);
            }
            if (nodeSection) {

                for (String t : nodeList) {
                    String id = "";
                    String lat = "";
                    String lon = "";

                    Pattern p = Pattern.compile(" id=\".*?\"");
                    Matcher m = p.matcher(t);
                    if (m.find()) {
                        String ss = m.group().replace("id=", "").replace("\"", "").trim();
                        id = ss;
                    }

                    p = Pattern.compile(" lat=\".*?\"");
                    m = p.matcher(t);
                    if (m.find()) {
                        String ss = m.group().replace("lat=", "").replace("\"", "").trim();
                        lat = ss;
                    }

                    p = Pattern.compile(" lon=\".*?\"");
                    m = p.matcher(t);
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
                nodeSection = false;
                nodeList.clear();
            }
        }
        ways.close();
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

    private static String translateEdge(List<String> section) throws IllegalArgumentException
    {
        String wayId = "";
        String nodes = "";
        String name = "";
        String type = "";

        Pattern p;
        Matcher m;
        boolean isWay = false;
        for (String s : section) {
            p = Pattern.compile("\\<way id\\=\".*?\"\\>");
            m = p.matcher(s);
            if (m.matches()) {
                isWay = true;
                //save wayId
                wayId = s.replaceAll("\\<way id\\=\"", "").replaceAll("\"\\>", "").trim();
            } else if (isWay) {
                p = Pattern.compile("\\<nd ref\\=\".*?\"\\/\\>");
                m = p.matcher(s);
                if (m.matches()) {
                    //save nodeId
                    nodes = nodes + s.replaceAll("\\<nd ref\\=\"", "").replaceAll("\"\\/\\>", "").trim() + "$ ";
                } else {
                    p = Pattern.compile("\\<tag k\\=\"name\" v\\=\".*?\"\\/\\>");
                    m = p.matcher(s);
                    if (m.matches()) {
                        //save roadName
                        name = s.replaceAll("\\<tag k\\=\"name\" v\\=\"", "").replaceAll("\"\\/\\>", "").trim();
                    } else {
                        p = Pattern.compile("\\<tag k\\=\"highway\" v\\=\".*?\"\\/\\>");
                        m = p.matcher(s);
                        if (m.matches()) {
                            //save roadType
                            type = s.replaceAll("\\<tag k\\=\"highway\" v\\=\"", "").replaceAll("\"\\/\\>", "").trim();
                        } else {
                            p = Pattern.compile("\\<\\/way\\>");
                            m = p.matcher(s);
                            if (m.matches()) {
                                return wayId + "$ " + name + "$ " + type + "$ " + nodes.substring(0, nodes.length() - 2);
                            }
                        }
                    }
                }
            }
        }
        throw new IllegalArgumentException("The section was not an edge");
    }
}
