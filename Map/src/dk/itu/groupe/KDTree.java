package dk.itu.groupe;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author pecto
 */
enum Dimension { X, Y };

public class KDTree {
    KDTree HIGH, LOW;
    List<NodeData> nodeData;
    List<EdgeData> edgeData;
    double split;
    double xmin, xmax, ymin, ymax;
    Dimension dim = Dimension.X;
    
    public KDTree(EdgeData edges, HashMap nodemap) {
        
        
    }
    
    public void doStuff(double x1, double y1, double x2, double y2) {
        if (dim == Dimension.X) {
            if (x2 < xmin || x1 > xmax) return;
        } else {
            if (y2 < ymin || y1 > ymax) return;
        }
        // do stuff to data
        if (HIGH != null) HIGH.doStuff(x1, y1, x2, y2);
        if (LOW != null) LOW.doStuff(x1, y1, x2, y2);
    }
}
    