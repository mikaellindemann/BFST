
public class OSMPrint
{

    public static void main(String[] args)
    {
        In in = new In("denmark-latest.osm");
        int i = 0;
        while (in.hasNextLine()) {
            String s = in.readLine();
            if (s.contains("217908713")) {
                StdOut.println(s);
                i = 10;
            } else if (i > 0) {
                StdOut.println(s);
                i--;
            }
            
        }
        in.close();
    }
}
