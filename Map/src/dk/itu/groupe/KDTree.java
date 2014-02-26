package dk.itu.groupe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author pecto
 */
public class KDTree {

    enum Dimension {

        X, Y
    };

    KDTree HIGH, LOW;
    List<EdgeData> edges;
    EdgeData centerEdge;
    double xmin, ymin, xmax, ymax;
    Dimension dim = Dimension.X;

    public KDTree(List<EdgeData> edges, HashMap<Integer, NodeData> nodeMap) {
        xmin = ymin = Double.MAX_VALUE;
        xmax = ymax = Double.MIN_VALUE;
        for (EdgeData edge : edges) {
            NodeData firstNode = nodeMap.get(edge.FNODE);
            NodeData lastNode = nodeMap.get(edge.TNODE);

            // Set xmin, xmax, ymin and ymax.
            for (NodeData nd : new NodeData[]{firstNode, lastNode}) {
                if (nd.X_COORD < xmin) {
                    xmin = nd.X_COORD;
                } else if (nd.X_COORD > xmax) {
                    xmax = nd.X_COORD;
                }
                if (nd.Y_COORD < ymin) {
                    ymin = nd.Y_COORD;
                } else if (nd.Y_COORD > ymax) {
                    ymax = nd.Y_COORD;
                }
            }
        }

        if (edges.size() <= 500) {
            this.edges = edges;
        } else {
            List<EdgeData> low = new ArrayList<>(), high = new ArrayList<>();
            centerEdge = edges.remove(edges.size() / 2);
            if (ymax - ymin < xmax - xmin) {
                dim = Dimension.X;
                // Put the right elements where it belongs.
                for (EdgeData edge : edges) {
                    if (nodeMap.get(edge.FNODE).X_COORD < nodeMap.get(centerEdge.FNODE).X_COORD) {
                        low.add(edge);
                    } else {
                        high.add(edge);
                    }
                }
            } else {
                dim = Dimension.Y;
                // Put the right elements where it belongs.
                for (EdgeData edge : edges) {
                    if (nodeMap.get(edge.FNODE).Y_COORD < nodeMap.get(centerEdge.FNODE).Y_COORD) {
                        low.add(edge);
                    } else {
                        high.add(edge);
                    }
                }
            }
            LOW = new KDTree(low, nodeMap);
            HIGH = new KDTree(high, nodeMap);
        }
    }

    public List<EdgeData> getEdges(double x1, double y1, double x2, double y2) {
        if (dim == Dimension.X) {
            if (x2 < xmin || x1 > xmax) {
                return new ArrayList<>();
            }
        } else {
            if (y2 < ymin || y1 > ymax) {
                return new ArrayList<>();
            }
        }

        if (HIGH == null && LOW == null) {
            return edges;
        } else {
            List<EdgeData> edgeList = LOW.getEdges(x1, y1, x2, y2);
            assert (centerEdge != null);
            edgeList.add(centerEdge);

            edgeList.addAll(HIGH.getEdges(x1, y1, x2, y2));
            return edgeList;
        }
    }

    public void doStuff(double x1, double y1, double x2, double y2) {
        if (dim == Dimension.X) {
            if (x2 < xmin || x1 > xmax) {
                return;
            }
        } else {
            if (y2 < ymin || y1 > ymax) {
                return;
            }
        }
        // do stuff to data
        if (HIGH != null) {
            HIGH.doStuff(x1, y1, x2, y2);
        }
        if (LOW != null) {
            LOW.doStuff(x1, y1, x2, y2);
        }
    }

    public static void main(String[] args) throws IOException {
        String dir = "./data/";

        // For this example, we'll simply load the raw data into
        // ArrayLists.
        final List<EdgeData> edgeList = new ArrayList<>();
        final HashMap<Integer, NodeData> nodeMap = new HashMap<>();

        // For that, we need to inherit from KrakLoader and override
        // processNode and processEdge. We do that with an 
        // anonymous class. 
        KrakLoader loader = new KrakLoader() {
            @Override
            public void processNode(NodeData nd) {
                nodeMap.put(nd.KDV, nd);
            }

            @Override
            public void processEdge(EdgeData ed) {
                edgeList.add(ed);
            }
        };

        // If your machine slows to a crawl doing inputting, try
        // uncommenting this. 
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        // Invoke the loader class.
        loader.load(dir + "kdv_node_unload.txt",
                dir + "kdv_unload.txt");

        KDTree tree = new KDTree(edgeList, nodeMap);
        System.out.println("List: " + edgeList.size());
        System.out.println("Tree: " + tree.getEdges(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE).size());
    }
}
