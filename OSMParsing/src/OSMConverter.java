
import java.io.PrintStream;


public class OSMConverter
{

    private static double radius = 6371;

    public static void main(String[] args)
    {
        Out out = new Out("nodesXY.csv");
        out.println("id, x, y");
        In in = new In("nodesNoTag.csv");
        in.readLine();
        while (in.hasNextLine()) {
            String[] sub = in.readLine().split(", ");
            double latitude = Double.parseDouble(sub[1]);
            double longitude = Double.parseDouble(sub[2]);

            double mapWidth = 450000;

            // get x value
            double x = Math.round(((longitude + 180) * (mapWidth / 360)) * 10000000) / 10000000.0;

            // convert from degrees to radians
            double latRad = latitude * Math.PI / 180;

            // get y value
            double mercN = Math.log(Math.tan((Math.PI / 4) + (latRad / 2)));
            double y = Math.round((mapWidth * mercN / (2 * Math.PI)) * 10000000) / 10000000.0;
            
            out.print(sub[0] + ", ");
            out.printf("%.7f", x);
            out.print(", ");
            out.printf("%.7f", y);
            out.println();
        }
    }
}
