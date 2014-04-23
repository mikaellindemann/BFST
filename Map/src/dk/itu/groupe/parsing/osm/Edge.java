package dk.itu.groupe.parsing.osm;

import dk.itu.groupe.OneWay;

/**
 * Represents the raw data from a line in edges.csv.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class Edge
{

    private final long id;
    private final OSMRoadType type;
    private final String roadname;
    private final int exitNumber;
    private final int speedLimit;
    private final OneWay oneWay;
    private final long[] nodeIds;

    @Override
    public String toString()
    {
        String s = id + ","
                + type.getTypeNo() + ",";
        if (roadname != null) {
            s += "`" + roadname + "`,";
        } else {
            s += ",";
        }
        s += exitNumber + ","
                + speedLimit + ","
                + oneWay.getNumber();
        for (long node : nodeIds) {
            s += "," + node;
        }
        return s;
    }

    public Edge(long id,
            OSMRoadType type,
            String roadname,
            int exitNumber,
            int speedLimit,
            OneWay oneWay,
            long[] nodes)
    {
        this.id = id;
        this.type = type;
        this.roadname = roadname;
        this.exitNumber = exitNumber;
        if (speedLimit == 0) {
            this.speedLimit = type.getSpeed();
        } else {
            this.speedLimit = speedLimit;
        }
        this.oneWay = oneWay;
        this.nodeIds = nodes;
    }

    public OSMRoadType getType()
    {
        return type;
    }

    public String getRoadname()
    {
        return roadname;
    }
    
    public long[] getNodeIds()
    {
        return nodeIds;
    }
}
