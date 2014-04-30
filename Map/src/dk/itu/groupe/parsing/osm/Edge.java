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
    private final long fromId, toId;

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder(id + "," + type.getTypeNo() + ",");
        if (roadname != null) {
            s.append("`").append(roadname).append("`,");
        } else {
            s.append(",");
        }
        s.append(String.format(Locale.ENGLISH, "%.2f", length)).append(",").append(exitNumber).append(",").append(speedLimit).append(",").append(String.format(Locale.ENGLISH, "%.2f", driveTime)).append(",").append(oneWay.getNumber()).append(",").append(fromId).append(",").append(toId);
        return s.toString();
    }

    public Edge(long id,
            OSMRoadType type,
            String roadname,
            double length,
            int exitNumber,
            int speedLimit,
            OneWay oneWay,
            long fromId, long toId)
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
        this.fromId = fromId;
        this.toId = toId;
    }

    public OSMRoadType getType()
    {
        return type;
    }

    public String getRoadname()
    {
        return roadname;
    }

    public long getFromId()
    {
        return fromId;
    }
    
    public long getToId()
    {
        return toId;
    }
}
