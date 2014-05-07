package dk.itu.groupe.parsing.krak;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Mikael
 */
public class KrakParser
{
    private static Set<Integer> usedNodes;
    private static Map<Integer, List<EdgeData>> edges;

    public static void main(String[] args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace(System.err);
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showOpenDialog(null);
        if (fileChooser.getSelectedFile() == null) {
            return;
        }
        String dir = fileChooser.getSelectedFile().getAbsolutePath();
        usedNodes = new HashSet<>();
        edges = new HashMap<>();
        final Map<Integer, NodeData> nodeMap = new HashMap<>();
        KrakLoader loader = new KrakLoader()
        {

            @Override
            public void processNode(NodeData nd)
            {
                nodeMap.put(nd.ID, nd);
            }

            @Override
            public void processEdge(EdgeData ed)
            {
                if (!edges.containsKey(ed.getType().getNewTypeNumber())) {
                    edges.put(ed.getType().getNewTypeNumber(), new LinkedList<EdgeData>());
                }
                edges.get(ed.getType().getNewTypeNumber()).add(ed);
            }
        };

        new File("./res/data/krak").mkdirs();

        try {
            loader.load(dir + "/kdv_node_unload.txt", dir + "/kdv_unload.txt", nodeMap);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "An unexpected error has occured.\nThis program will exit.",
                    "Error loading",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(System.err);
            System.exit(300);
        }
        double xMin, xMax, yMin, yMax;
        xMin = yMin = Double.MAX_VALUE;
        xMax = yMax = Double.MIN_VALUE;

        for (Iterator<List<EdgeData>> it = edges.values().iterator(); it.hasNext();) {
            List<EdgeData> edgeList = it.next();
            KrakRoadType rt = edgeList.get(0).getType();
            try (DataOutputStream edgeStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("./res/data/krak/edges" + rt.getNewTypeNumber() + ".bin")))) {
                for (EdgeData ed : edgeList) {
                    usedNodes.add(ed.FNODE);
                    usedNodes.add(ed.TNODE);
                    edgeStream.writeInt(ed.getType().getNewTypeNumber());
                    if (ed.VEJNAVN == null) {
                        edgeStream.writeUTF("");
                    } else {
                        edgeStream.writeUTF(ed.VEJNAVN);
                    }
                    edgeStream.writeFloat((float)ed.LENGTH);
                    edgeStream.writeFloat((float)ed.DRIVETIME);
                    edgeStream.writeInt(ed.ONE_WAY.getNumber());
                    edgeStream.writeInt(2);
                    edgeStream.writeInt(ed.FNODE);
                    edgeStream.writeInt(ed.TNODE);
                }
                it.remove();
                edgeStream.close();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
        assert edges.isEmpty();

        try (DataOutputStream nodeStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("./res/data/krak/nodes.bin")))) {
            for (Integer i : usedNodes) {
                NodeData n = nodeMap.get(i);
                xMin = Math.min(n.X_COORD, xMin);
                xMax = Math.max(n.X_COORD, xMax);
                yMin = Math.min(n.Y_COORD, yMin);
                yMax = Math.max(n.Y_COORD, yMax);
                nodeStream.writeInt(n.ID);
                nodeStream.writeFloat((float)n.X_COORD);
                nodeStream.writeFloat((float)n.Y_COORD);
            }
            nodeStream.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

        try (DataOutputStream info = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("./res/data/krak/info.bin")))) {
            info.writeDouble(xMin);
            info.writeDouble(yMin);
            info.writeDouble(xMax);
            info.writeDouble(yMax);
            info.writeInt(usedNodes.size() + 1);
            info.writeInt(edges.size() - 1);
            info.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
