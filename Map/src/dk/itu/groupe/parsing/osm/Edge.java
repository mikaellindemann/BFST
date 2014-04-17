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

    private final Node fromNode;
    private final Node toNode;
    private final double length;
    private final long id;
    private final OSMRoadType type;
    private final String roadname;
    private final int exitNumber;
    private final int speedLimit;
    private final double driveTime;
    private final OneWay oneWay;

    @Override
    public String toString()
    {
        String s = fromNode.getId() + ","
                + toNode.getId() + ","
                + String.format(Locale.ENGLISH, "%.2f,", length)
                + id + ","
                + type.getTypeNo() + ",";
        if (roadname != null) {
            s += "`" + roadname + "`,";
        } else {
            s += ",";
        }
        s += exitNumber + ","
                + speedLimit + ","
                + String.format(Locale.ENGLISH, "%.2f,", driveTime)
                + oneWay.getNumber();
        return s;
    }

    public Edge(Node fromNode,
            Node toNode,
            long id,
            OSMRoadType type,
            String roadname,
            int exitNumber,
            int speedLimit,
            OneWay oneWay)
    {
        this.fromNode = fromNode;
        this.toNode = toNode;
        length = fromNode.getPoint().distance(toNode.getPoint());
        this.id = id;
        this.type = type;
        this.roadname = roadname;
        this.exitNumber = exitNumber;
        this.speedLimit = speedLimit;
        if (speedLimit != 0) {
            // length is in meters.
            driveTime = length / (speedLimit * 1000 / 60) * 1.15;
            // driveTime is now in minutes.
        } else {
            driveTime = length / (type.getSpeed() * 1000 / 60) * 1.15;
        }
        this.oneWay = oneWay;
    }

    public OSMRoadType getType()
    {
        return type;
    }

    public String getRoadname()
    {
        return roadname;
    }
}
