package dk.itu.groupe.parsing.osm;

import dk.itu.groupe.OneWay;
import java.util.Locale;

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
    private final double length;
    private final int exitNumber;
    private final int speedLimit;
    private final double driveTime;
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
        s += String.format(Locale.ENGLISH, "%.2f", length) + ","
                + exitNumber + ","
                + speedLimit + ","
                + String.format(Locale.ENGLISH, "%.2f", driveTime) + ","
                + oneWay.getNumber();
        for (long node : nodeIds) {
            s += "," + node;
        }
        return s;
    }

    public Edge(long id,
            OSMRoadType type,
            String roadname,
            double length,
            int exitNumber,
            int speedLimit,
            OneWay oneWay,
            long[] nodes)
    {
        this.id = id;
        this.type = type;
        this.roadname = roadname;
        this.length = length;
        this.exitNumber = exitNumber;
        if (speedLimit == 0) {
            this.speedLimit = type.getSpeed();
        } else {
            this.speedLimit = speedLimit;
        }
        driveTime = (length / (this.speedLimit * 1000 / 60)) * 1.15;
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
