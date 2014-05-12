package dk.itu.groupe.data;

import dk.itu.groupe.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Mikael
 */
public class KDTreeTest
{

    public KDTreeTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testNoEdge()
    {
        LinkedList<Edge> edges = new LinkedList<>();
        boolean exception = false;
        try {
            KDTree instance = new KDTree(edges, 0, 0, 0, 0);
        } catch (Exception ex) {
            if (ex.getClass() == IndexOutOfBoundsException.class) {
                exception = true;
            }
        }
        Assert.assertEquals(true, exception);
    }

    @Test
    public void testXY()
    {
        LinkedList<Edge> edges = new LinkedList<>();
        Node first = new Node(0, 0, 0);
        Node second = new Node(1, 4, 1);
        Node third = new Node(2, 1, 3);
        Node fourth = new Node(3, 4, 5);
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[] {first, second}));
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[] {third, first}));
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[] {third, second}));
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[] {fourth, first}));
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[] {fourth, third}));
        KDTree instance = new KDTree(edges, 0, 0, 4, 5);
        // If X is set, it means that the if statement at Label 1 returned true.
        // If Y is set, it means that the if statement at Label 1 returned false.
        assertEquals(KDTree.Dimension.Y, instance.dim);
        // If LOW is set inside a "Label 1 = false"-run means that there were at
        // least 1 edge, that had a Y-coordinate that was less than the
        // split-elements Y-coordinate.
        assertEquals(KDTree.Dimension.X, instance.LOW.dim);
        // As the next one returns null, it means that there were no edges with
        // an X-coordinate less than the split-elements.
        assertEquals(null, instance.LOW.LOW);
        assertEquals(KDTree.Dimension.X, instance.LOW.HIGH.dim);
        assertEquals(null, instance.LOW.HIGH.LOW);
        assertEquals(null, instance.LOW.HIGH.HIGH);
        assertEquals(KDTree.Dimension.X, instance.HIGH.dim);
        assertEquals(KDTree.Dimension.Y, instance.HIGH.LOW.dim);
        assertEquals(null, instance.HIGH.LOW.LOW);
        assertEquals(null, instance.HIGH.LOW.HIGH);
        assertEquals(null, instance.HIGH.HIGH);
    }
    
    @Test
    public void testXinLow()
    {
        LinkedList<Edge> edges = new LinkedList<>();
        Node first = new Node(0, 0, 0);
        Node second = new Node(1, 4, 1);
        Node third = new Node(2, 5, 1);
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[] {first, second}));
        edges.add(new Edge(null, null, 0, 0, OneWay.NO, new Node[] {first, third}));
        KDTree instance = new KDTree(edges, 0, 0, 5, 2);
        assertEquals(KDTree.Dimension.X, instance.dim);
        assertEquals(KDTree.Dimension.X, instance.LOW.dim);
        assertEquals(null, instance.LOW.LOW);
        assertEquals(null, instance.LOW.HIGH);
        assertEquals(null, instance.HIGH);
    }
}
