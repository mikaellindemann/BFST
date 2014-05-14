package dk.itu.groupe.parsing.krak;

import dk.itu.groupe.data.OneWay;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the raw data from a line in kdv_unload.txt.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) &amp;
 * Mikael Jepsen (mlin@itu.dk)
 */
public class EdgeData
{

    private final static HashMap<Integer, KrakRoadType> rtMap;
    private final static HashMap<String, OneWay> oneWayMap;

    static {
        rtMap = new HashMap<>();
        for (KrakRoadType rt : KrakRoadType.values()) {
            rtMap.put(rt.getTypeNumber(), rt);
        }
        oneWayMap = new HashMap<>();
        oneWayMap.put("", OneWay.NO);
        oneWayMap.put("ft", OneWay.FROM_TO);
        oneWayMap.put("tf", OneWay.TO_FROM);
        oneWayMap.put("n", OneWay.NO);
    }

    final int FNODE;
    final int TNODE;
    final double LENGTH;
    final int DAV_DK;
    final KrakRoadType TYPE;
    final String VEJNAVN;
    final int SPEED;
    final double DRIVETIME;
    final OneWay ONE_WAY;

    public EdgeData(String line, Map<Integer, NodeData> nodeMap)
    {
        DataLine dl = new DataLine(line);
        FNODE = dl.getInt();
        TNODE = dl.getInt();
        LENGTH = dl.getDouble();
        DAV_DK = dl.getInt();
        dl.getInt();
        int typ = dl.getInt();
        TYPE = rtMap.get(typ);
        if (TYPE == null) {
            System.err.println(typ);
            assert (TYPE != null);
        }
        String s = dl.getString();
        if (s != null) {
            VEJNAVN = s;
        } else {
            VEJNAVN = "";
        }
        dl.getInt();
        dl.getInt();
        dl.getInt();
        dl.getInt();
        dl.getString();
        dl.getString();
        dl.getString();
        dl.getString();
        dl.getInt();
        dl.getInt();
        dl.getInt();
        dl.getInt();
        dl.getInt();
        dl.getInt();
        dl.getInt();
        dl.getString();
        dl.getInt();
        dl.getInt();
        SPEED = dl.getInt();
        DRIVETIME = dl.getDouble();
        ONE_WAY = oneWayMap.get(dl.getString());
        dl.getString();
        dl.getString();
        dl.getInt();
        dl.getString();
        dl.getInt();
    }

    public KrakRoadType getType()
    {
        return TYPE;
    }
}
