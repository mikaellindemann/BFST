package dk.itu.groupe.parsing.krak;

import dk.itu.groupe.OneWay;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the raw data from a line in kdv_unload.txt.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class EdgeData
{

    private static HashMap<Integer, KrakRoadType> rtMap;
    private static HashMap<String, OneWay> oneWayMap;

    public final int FNODE;
    public final int TNODE;
    public final double LENGTH;
    public final int DAV_DK;
    private final KrakRoadType TYPE;
    public final String VEJNAVN;
    /*public final int FROMLEFT;
     public final int TOLEFT;
     public final int FROMRIGHT;
     public final int TORIGHT;
     public final String FROMLEFT_BOGSTAV;
     public final String TOLEFT_BOGSTAV;
     public final String FROMRIGHT_BOGSTAV;
     public final String TORIGHT_BOGSTAV;
     public final int V_POSTNR;
     public final int H_POSTNR;*/
    public final int FRAKOERSEL;
    public final int SPEED;
    public final double DRIVETIME;
    public final OneWay ONE_WAY;
    /*public final String F_TURN;
     public final String T_TURN;*/
    public final Line2D line;

    @Override
    public String toString()
    {
        return /*FNODE + ","
                + TNODE + ","
                + LENGTH + ","
                + */DAV_DK + ","
                + TYPE.getNewTypeNumber() + ","
                + "`" + VEJNAVN + "`,"
                + FRAKOERSEL + ","
                + SPEED + ","
                //+ DRIVETIME + ","
                + ONE_WAY.getNumber() + ","
                + FNODE + "," + TNODE;
    }

    public EdgeData(String line, Map<Integer, NodeData> nodeMap)
    {
        if (rtMap == null) {
            rtMap = new HashMap<>();
            for (KrakRoadType rt : KrakRoadType.values()) {
                rtMap.put(rt.getTypeNumber(), rt);
            }
        }
        if (oneWayMap == null) {
            oneWayMap = new HashMap<>();
            oneWayMap.put("", OneWay.NO);
            oneWayMap.put("ft", OneWay.FROM_TO);
            oneWayMap.put("tf", OneWay.TO_FROM);
            oneWayMap.put("n", OneWay.NO);
        }
        DataLine dl = new DataLine(line);
        FNODE = dl.getInt();
        TNODE = dl.getInt();
        NodeData fN = nodeMap.get(FNODE);
        NodeData tN = nodeMap.get(TNODE);
        LENGTH = dl.getDouble();
        DAV_DK = dl.getInt();
        dl.getInt();
        int typ = dl.getInt();
        TYPE = rtMap.get(typ);
        if (TYPE == null) {
            System.err.println(typ);
            assert (TYPE != null);
        }
        VEJNAVN = dl.getString();
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
        FRAKOERSEL = dl.getInt();
        dl.getInt();
        SPEED = dl.getInt();
        DRIVETIME = dl.getDouble();
        ONE_WAY = oneWayMap.get(dl.getString());
        dl.getString();
        dl.getString();
        dl.getInt();
        dl.getString();
        dl.getInt();

        this.line = new Line2D.Double(
                fN.X_COORD,
                fN.Y_COORD,
                tN.X_COORD,
                tN.Y_COORD);
    }

    public KrakRoadType getType()
    {
        return TYPE;
    }
}
