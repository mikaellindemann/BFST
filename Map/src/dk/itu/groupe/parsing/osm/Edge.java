package dk.itu.groupe.parsing.osm;

import dk.itu.groupe.data.OneWay;

/**
 * Represents the raw data from a line in edges.csv.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class Edge
{

    private final OSMRoadType type;
    private final String roadname;
    private final int exitNumber;
    private final int speedLimit;
    private final OneWay oneWay;
    private final long[] nodeIds;

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder(type.getTypeNo() + ",");
        if (roadname != null) {
            s.append("`").append(roadname).append("`,");
        } else {
            s.append(",");
        }
        s.append(exitNumber).append(",").append(speedLimit).append(",").append(oneWay.getNumber());
        for (long l : nodeIds) {
            s.append(",").append(l);
        }
        return s.toString();
    }

    public Edge(OSMRoadType type,
            String roadname,
            int exitNumber,
            int speedLimit,
            OneWay oneWay,
            long[] nodeIds)
    {
        this.type = type;
        if (roadname != null) {
            this.roadname = roadname;
        } else {
            this.roadname = "";
        }
        this.exitNumber = exitNumber;
        if (speedLimit == 0) {
            this.speedLimit = type.getSpeed();
        } else {
            this.speedLimit = speedLimit;
        }
        this.oneWay = oneWay;
        this.nodeIds = nodeIds;
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

    public int getExitNumber()
    {
        return exitNumber;
    }

    public int getSpeedLimit()
    {
        return speedLimit;
    }

    public OneWay getOneWay()
    {
        return oneWay;
    }

}
