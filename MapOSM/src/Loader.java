
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Loader implements Runnable
{

    private final String dir = "./data/";
    private final boolean node;
    private Map<Long, Node> nodemap;
    private RoadType rt;
    private Map<RoadType, Set<Way>> waymap;

    public Loader(RoadType rt, Map<RoadType, Set<Way>> waymap)
    {
        this.rt = rt;
        this.waymap = waymap;
        node = false;
    }
    
    public Loader(Map<Long, Node> nodemap)
    {
        node = true;
        this.nodemap = nodemap;
    }

    @Override
    public void run()
    {
        if (node) {
            try (Scanner in = new Scanner(new File(dir + "nodes.csv"))) {
                in.nextLine();
                while (in.hasNextLine()) {
                    Node n = Node.parseNode(in.nextLine());
                    nodemap.put(n.getId(), n);
                }
                assert(!nodemap.isEmpty());
                System.out.println("Nodes has been loaded");
            } catch (FileNotFoundException ex) {

            }
        } else {
            try (Scanner in = new Scanner(new File(dir + "way-" + rt.toString() + ".csv"))) {
                while (in.hasNextLine()) {
                    String s = in.nextLine();

                    Way way = Way.parseWay(s, rt);
                    if (waymap.get(way.getType()) == null) {
                        waymap.put(way.getType(), new HashSet<Way>());
                    }
                    waymap.get(way.getType()).add(way);
                }
                System.out.println(rt.toString() + " has been loaded");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace(System.err);
            }
        }
        System.gc();
    }
}
