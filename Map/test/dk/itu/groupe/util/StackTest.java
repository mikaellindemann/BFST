package dk.itu.groupe.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mikael
 */
public class StackTest
{

    /**
     * Test of push method, of class Stack.
     */
    @Test
    public void testPush()
    {
        Stack<String> instance = new Stack<>();
        assertSame(0, instance.size());
        assertTrue(instance.isEmpty());
        instance.push("One");
        assertSame(1, instance.size());
        assertFalse(instance.isEmpty());
        String value = instance.pop();
        assertEquals("One", value);
    }

    /**
     * Test of peek method, of class Stack.
     */
    @Test
    public void testPeek()
    {
        Stack<String> instance = new Stack<>();
        Throwable caught = null;
        try {
            instance.peek();
        } catch (Exception ex) {
            caught = ex;
        }
        assertNotNull(caught);
        assertSame(IndexOutOfBoundsException.class, caught.getClass());
        instance.push("Hello");
        assertEquals("Hello", instance.peek());
        instance.push("How are you?");
        assertEquals("How are you?", instance.peek());
    }

    /**
     * Test of pop method, of class Stack.
     */
    @Test
    public void testPop()
    {
        Stack<String> instance = new Stack<>();
        Throwable caught = null;
        try {
            instance.pop();
        } catch (Exception ex) {
            caught = ex;
        }
        assertNotNull(caught);
        assertSame(IndexOutOfBoundsException.class, caught.getClass());
        String[] strings = new String[]{"One", "Two", "Three"};
        instance.push(strings[0]);
        instance.push(strings[1]);
        instance.push(strings[2]);
        for (int i = strings.length - 1; !instance.isEmpty(); i--) {
            assertSame(strings[i], instance.pop());
        }
    }

    /**
     * Test of isEmpty method, of class Stack.
     */
    @Test
    public void testIsEmpty()
    {
        Stack<String> instance = new Stack<>();
        assertTrue(instance.isEmpty());
        instance.push("Not Empty");
        assertFalse(instance.isEmpty());
        instance.peek();
        assertFalse(instance.isEmpty());
        instance.pop();
        assertTrue(instance.isEmpty());
    }

    /**
     * Test of size method, of class Stack.
     */
    @Test
    public void testSize()
    {
        Stack<String> instance = new Stack<>();
        assertSame(0, instance.size());
        instance.push("Hello");
        assertSame(1, instance.size());
        instance.push("How");
        assertSame(2, instance.size());
        instance.peek();
        assertSame(2, instance.size());
        instance.pop();
        assertSame(1, instance.size());
    }

}
