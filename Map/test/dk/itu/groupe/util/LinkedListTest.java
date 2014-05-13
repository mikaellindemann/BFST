package dk.itu.groupe.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mikael
 */
public class LinkedListTest
{

    public LinkedListTest()
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

    /**
     * Test of add method, of class LinkedList.
     */
    @Test
    public void testAdd()
    {
        String value = "First";
        LinkedList<String> instance = new LinkedList<>();
        // A LinkedList is empty when it is created.
        assertTrue(instance.isEmpty());

        // But when a value is added...
        instance.add(value);
        // It is no longer empty.
        assertSame(1, instance.size());

        /*
         * Just to make sure we don't do something crazy, it is checked that the
         * element that we added, is the same as the one coming out of the list
         * again.
         */
        assertSame(value, instance.getLast());
        assertSame(value, instance.get(0));
    }

    /**
     * Test of isEmpty method, of class LinkedList.
     */
    @Test
    public void testIsEmpty()
    {
        LinkedList<String> instance = new LinkedList<>();
        // As said above, a new LinkedList is empty.
        assertTrue(instance.isEmpty());
        instance.add("Testing isEmpty");
        // But not after the first element is added.
        assertFalse(instance.isEmpty());
        // As the LinkedList doesn't have methods to remove elements, this isn't
        // tested.
    }

    /**
     * Test of size method, of class LinkedList.
     */
    @Test
    public void testSize()
    {
        LinkedList<Object> instance = new LinkedList<>();
        // The size of a new LinkedList should be 0.
        assertSame(0, instance.size());
        instance.add(this);
        // And it should increment by one every time we add an element.
        assertSame(1, instance.size());
        instance.add(this);
        // The same element can even be in the list multiple times.
        assertSame(2, instance.size());
    }

    /**
     * Test of toArray method, of class LinkedList.
     */
    @Test
    public void testToArray()
    {
        LinkedList<String> instance = new LinkedList<>();
        String[] strings = new String[]{"First", "Second", "Third", "ÆØÅ"};
        // An empty list should return an array of length 0.
        assertArrayEquals(new String[0], instance.toArray());
        instance.add(strings[0]);
        instance.add(strings[1]);
        instance.add(strings[2]);
        instance.add(strings[3]);
        // When all the elements of an array has been added to the list in the
        // right order, they should be equal!
        assertArrayEquals(strings, instance.toArray());
    }

    /**
     * Test of get method, of class LinkedList.
     */
    @Test
    public void testGet()
    {
        LinkedList<Object> instance = new LinkedList<>();
        Throwable caught = null;
        try {
            instance.get(0);
        } catch (Exception ex) {
            caught = ex;
        }
        assertNotNull(caught);
        // A list without elements throws an exception when the get-method is
        // called.
        assertSame(IndexOutOfBoundsException.class, caught.getClass());
        caught = null;
        instance.add(this);
        // But when an element is in the list on the index supplied, it should
        // be returned.
        assertSame(this, instance.get(0));
        try {
            instance.get(-1);
        } catch (Exception ex) {
            caught = ex;
        }
        assertNotNull(caught);
        // But if we call the get()-method with wrong indexes, which is indexes
        // less than 0 or indexes greater than or equal to the size of the list
        // we get exceptions as well:
        assertSame(IndexOutOfBoundsException.class, caught.getClass());
        caught = null;
        try {
            instance.get(1);
        } catch (Exception ex) {
            caught = ex;
        }
        assertNotNull(caught);
        assertSame(IndexOutOfBoundsException.class, caught.getClass());
    }

    /**
     * Test of getLast method, of class LinkedList.
     */
    @Test
    public void testGetLast()
    {
        LinkedList<Object> instance = new LinkedList<>();
        Throwable caught = null;
        try {
            instance.getLast();
        } catch (Exception ex) {
            caught = ex;
        }
        assertNotNull(caught);
        // When the list is empty there is no last element. Therefore an
        // exception is thrown.
        assertSame(IndexOutOfBoundsException.class, caught.getClass());
        Object element = new Object();
        instance.add(element);
        // But if there is elements in the list, it will always return the one
        // that was added most recently.
        assertSame(element, instance.getLast());
        String one = "one";
        String two = "two";
        instance.add(two);
        instance.add(one);
        assertSame(one, instance.getLast());
    }

    /**
     * Test of iterator method, of class LinkedList.
     */
    @Test
    public void testIterator()
    {
        LinkedList<String> instance = new LinkedList<>();
        Iterator<String> it = instance.iterator();
        // An empty list should return an iterator with no elements.
        assertFalse(it.hasNext());
        Throwable caught = null;
        try {
            it.next();
        } catch (NoSuchElementException ex) {
            caught = ex;
        }
        assertNotNull(caught);
        // So when the next()-method of the iterator is called, it will throw an
        // exception.
        assertSame(NoSuchElementException.class, caught.getClass());
        String[] strings = new String[]{"First", "Second", "Third"};
        instance.add(strings[0]);
        instance.add(strings[1]);
        instance.add(strings[2]);
        it = instance.iterator();
        // But once it has some elements, it will return them in the order they
        // were added.
        int i = 0;
        while (it.hasNext()) {
            assertSame(strings[i++], it.next());
        }
    }
}
