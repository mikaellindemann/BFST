package dk.itu.groupe;

import java.awt.geom.Line2D;
import java.util.HashMap;

/**
 * Represents the raw data from a line in kdv_unload.txt.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class Edge
{

    public final double LENGTH;
    public final int DAV_DK;
    private RoadType TYPE;
    public final String VEJNAVN;
    public final int FROMLEFT;
    public final int TOLEFT;
    public final int FROMRIGHT;
    public final int TORIGHT;
    public final String FROMLEFT_BOGSTAV;
    public final String TOLEFT_BOGSTAV;
    public final String FROMRIGHT_BOGSTAV;
    public final String TORIGHT_BOGSTAV;
    public final int V_POSTNR;
    public final int H_POSTNR;
    public final int FRAKOERSEL;
    public final int ZONE;
    public final int SPEED;
    public final double DRIVETIME;
    public final String ONE_WAY;
    public final String F_TURN;
    public final String T_TURN;
    public final Line2D line;

    @Override
    public String toString()
    {
        return String.format("%.5f,", LENGTH)
                + DAV_DK + ","
                + TYPE + ","
                + "'" + VEJNAVN + "',"
                + FROMLEFT + ","
                + TOLEFT + ","
                + FROMRIGHT + ","
                + TORIGHT + ","
                + "'" + FROMLEFT_BOGSTAV + "',"
                + "'" + TOLEFT_BOGSTAV + "',"
                + "'" + FROMRIGHT_BOGSTAV + "',"
                + "'" + TORIGHT_BOGSTAV + ","
                + V_POSTNR + ","
                + H_POSTNR + "',"
                + FRAKOERSEL + ","
                + ZONE + ","
                + SPEED + ","
                + String.format("%.3f,", DRIVETIME)
                + "'" + ONE_WAY + "',"
                + "'" + F_TURN + "',"
                + "'" + T_TURN;
    }

    public Edge(String line, HashMap<Integer, Node> nodeMap)
    {
        DataLine dl = new DataLine(line);
        Node fN = nodeMap.get(dl.getInt());
        Node tN = nodeMap.get(dl.getInt());
        LENGTH = dl.getDouble();
        DAV_DK = dl.getInt();
        dl.getInt();
        int typ = dl.getInt();
        for (RoadType rt : RoadType.values()) {
            if (rt.getTypeNumber() == typ) {
                TYPE = rt;
                break;
            }
        }
        VEJNAVN = dl.getString();
        FROMLEFT = dl.getInt();
        TOLEFT = dl.getInt();
        FROMRIGHT = dl.getInt();
        TORIGHT = dl.getInt();
        FROMLEFT_BOGSTAV = dl.getString();
        TOLEFT_BOGSTAV = dl.getString();
        FROMRIGHT_BOGSTAV = dl.getString();
        TORIGHT_BOGSTAV = dl.getString();
        dl.getInt();
        dl.getInt();
        V_POSTNR = dl.getInt();
        H_POSTNR = dl.getInt();
        dl.getInt();
        dl.getInt();
        dl.getInt();
        dl.getString();
        FRAKOERSEL = dl.getInt();
        ZONE = dl.getInt();
        SPEED = dl.getInt();
        DRIVETIME = dl.getDouble();
        ONE_WAY = dl.getString();
        F_TURN = dl.getString();
        T_TURN = dl.getString();
        dl.getInt();
        dl.getString();
        dl.getInt();

        this.line = new Line2D.Double(
                fN.X_COORD,
                fN.Y_COORD,
                tN.X_COORD,
                tN.Y_COORD);
    }

    public RoadType getType()
    {
        return TYPE;
    }
}
