
import java.util.HashSet;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mikael
 */
public class CheckNodes
{
    public static void main(String[] args)
    {
        Set<Long> nodeIds = new HashSet<>();
        for (RoadType rt : RoadType.values()) {
            In in = new In("./data/way-" + rt.toString() + ".csv");
            while (in.hasNextLine()) {
                String[] sub = in.readLine().split("\\$ ");
                for (int i = 3; i < sub.length; i++) {
                    nodeIds.add(Long.parseLong(sub[i]));
                }
            }
        }
        In in = new In("./data/nodesXY.csv");
        Out out = new Out("./data/nodesXYNoSpill.csv");
        out.println(in.readLine());
        while (in.hasNextLine()) {
            String s = in.readLine();
            if(nodeIds.contains(Long.parseLong(s.split(", ")[0]))) {
                out.println(s);
            }
        }
        in.close();
        out.close();
    }
}
