
import java.util.regex.*;
import java.util.HashSet;
import java.util.Arrays;

public class WayTag
{

    public static void main(String[] args)
    {
        HashSet<String> tag = new HashSet<>();

        int index = 0;
        Pattern p = Pattern.compile("\\<tag k\\=\"highway\" v\\=\".*?\"");
        Matcher m;
        In in = new In("denmark-latest.osm");
        while (in.hasNextLine()) {
            String s = in.readLine();
            m = p.matcher(s);
            if (m.find()) {
                tag.add(s.replaceAll("\\<tag k\\=\"highway\" v\\=\"", "").replaceAll("\"\\/\\>", "").trim());;
            }
        }
        Out tags = new Out("highway tags.txt");
        String[] highwayTags = new String[tag.size()];
        for (String string : tag) {
            highwayTags[index++] = string;
        }
        Arrays.sort(highwayTags);
        for (String string : highwayTags) {
            tags.println(string);
        }
        tags.close();
    }
}
