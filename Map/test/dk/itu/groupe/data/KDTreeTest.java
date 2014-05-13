package dk.itu.groupe.data;

import dk.itu.groupe.util.LinkedList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import org.junit.Test;

/**
 *
 * @author Mikael
 */
public class KDTreeTest
{

    /*
     * These three tests covers every branch of the constructor of KDTrees.
     */
    @Test
    public void testNoEdge()
    {
        LinkedList<Edge> edges = new LinkedList<>();
        Throwable caught = null;
        try {
            new KDTree(edges, 0, 0, 0, 0);
        } catch (Exception ex) {
            caught = ex;
        }
        assertNotNull(caught);
        assertSame(IndexOutOfBoundsException.class, caught.getClass());
    }

    @Test
    public void testXY()
    {
        LinkedList<Edge> edges = new LinkedList<>();
        Node first = new Node(0, 0, 0);
        Node second = new Node(1, 4, 1);
        Node third = new Node(2, 1, 3);
        Node fourth = new Node(3, 4, 5);
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[]{first, second}));
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[]{third, first}));
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[]{third, second}));
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[]{fourth, first}));
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[]{fourth, third}));
        KDTree instance = new KDTree(edges, 0, 0, 4, 5);
        // If X is set, it means that the if statement at Label 1 returned true.
        // If Y is set, it means that the if statement at Label 1 returned false.
        assertSame(KDTree.Dimension.Y, instance.dim);
        assertSame(edges.get(2), instance.splitEdge);
        // If LOW is set inside a "Label 1 = false"-run means that there were at
        // least 1 edge, that had a Y-coordinate that was less than the
        // split-elements Y-coordinate.
        assertSame(KDTree.Dimension.X, instance.LOW.dim);
        assertSame(edges.get(1), instance.LOW.splitEdge);
        // As the next one returns null, it means that there were no edges with
        // an X-coordinate less than the split-elements.
        assertNull(instance.LOW.LOW);
        // If our KDTree followed the convention of the dimensions based on the
        // depth of the tree, the next assertion would be false. But our tree
        // uses the actual space it covers to find which dimension to split on.
        assertSame(KDTree.Dimension.X, instance.LOW.HIGH.dim);
        assertSame(edges.get(0), instance.LOW.HIGH.splitEdge);
        assertNull(instance.LOW.HIGH.LOW);
        assertNull(instance.LOW.HIGH.HIGH);
        assertSame(KDTree.Dimension.X, instance.HIGH.dim);
        assertSame(edges.get(4), instance.HIGH.splitEdge);
        assertSame(KDTree.Dimension.Y, instance.HIGH.LOW.dim);
        assertSame(edges.get(3), instance.HIGH.LOW.splitEdge);
        assertNull(instance.HIGH.LOW.LOW);
        assertNull(instance.HIGH.LOW.HIGH);
        assertNull(instance.HIGH.HIGH);
    }

    @Test
    public void testXinLow()
    {
        LinkedList<Edge> edges = new LinkedList<>();
        Node first = new Node(0, 0, 0);
        Node second = new Node(1, 4, 1);
        Node third = new Node(2, 5, 1);
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[]{first, second}));
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[]{first, third}));
        KDTree instance = new KDTree(edges, 0, 0, 5, 2);
        assertEquals(KDTree.Dimension.X, instance.dim);
        assertEquals(KDTree.Dimension.X, instance.LOW.dim);
        assertEquals(null, instance.LOW.LOW);
        assertEquals(null, instance.LOW.HIGH);
        assertEquals(null, instance.HIGH);
    }
}
