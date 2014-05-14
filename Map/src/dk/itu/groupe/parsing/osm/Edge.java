package dk.itu.groupe.parsing.osm;

import dk.itu.groupe.data.OneWay;

/**
 * Represents the raw data from a line in edges.csv.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) &amp;
 * Mikael Jepsen (mlin@itu.dk)
 */
public class Edge
{

    final OSMRoadType type;
    final String roadname;
    final int speedLimit;
    final OneWay oneWay;
    final long[] nodeIds;

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
        if (speedLimit == 0) {
            this.speedLimit = type.getSpeed();
        } else {
            this.speedLimit = speedLimit;
        }
        this.oneWay = oneWay;
        this.nodeIds = nodeIds;
    }
}
