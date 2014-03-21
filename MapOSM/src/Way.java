
import java.util.HashMap;
import java.util.Map;

public class Way
{

    private final long id;
    private final String name;
    private final RoadType type;
    private final long[] nodeIds;
    private static Map<String, String> interner = new HashMap<>();

    private Way(long id, String name, RoadType type, long[] nodeIds)
    {
        this.id = id;
        this.name = name;
        this.type = type;
        this.nodeIds = nodeIds;
    }

    public long getID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public RoadType getType()
    {
        return type;
    }

    public long[] getNodeIds()
    {
        return nodeIds;
    }

    @Override
    public String toString()
    {
        return id + " " + name + " " + type + " number of nodes:" + nodeIds.length;
    }

    private static String intern(String s)
    {
        if (interner.get(s) == null) {
            interner.put(s, s);
        }
        return interner.get(s);
    }

    public static void resetInterner()
    {
        interner = new HashMap<>();
    }

    public static Way parseWay(String s, RoadType type)
    {
        String[] sub = s.split("\\$ ");
        long id;
        id = Long.parseLong(sub[0]);
        String name = intern(sub[1]);
        try {
            if (type != RoadType.NOTAG) {
                assert (type.toString().equals(sub[2]));
            } else {
                assert ("".equals(sub[2]));
            }
        } catch (AssertionError e) {
            System.err.println(s);
            System.err.println(sub[2] + " should equal " + type);
            throw e;
        }
        long[] nodeIds = new long[sub.length - 3];
        for (int i = 3; i < sub.length; i++) {
            nodeIds[i - 3] = Long.parseLong(sub[i]);
        }
        return new Way(id, name, type, nodeIds);
    }
}
