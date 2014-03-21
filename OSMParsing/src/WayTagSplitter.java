
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WayTagSplitter
{

    private static ArrayList<String> tags;

    public static void main(String[] args)
    {
        tags = new ArrayList<>();
        In t = new In("highway tags.txt");
        while (t.hasNextLine()) {
            tags.add(t.readLine());
        }
        t.close();
        Map<String, Out> outs = new HashMap<>();
        for (String tag : tags) {
            outs.put(tag, new Out("ways/way-" + tag + ".csv"));
        }
        outs.put("notag", new Out("ways/way-notag.csv"));

        In in = new In(new File("ways.csv"));
        while (in.hasNextLine()) {
            boolean printed = false;
            String s = in.readLine();
            String[] sub = s.split("\\$ ");
            for (String tag : tags) {
                try {
                    if (sub[2].equals(tag)) {
                        outs.get(tag).println(s);
                        printed = true;
                        break;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.println(s);
                    throw ex;
                }
            }
            if (printed == false) {
                outs.get("notag").println(s);
            }
        }
        for (Out out : outs.values()) {
            out.close();
        }
    }
}
