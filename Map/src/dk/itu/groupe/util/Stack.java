package dk.itu.groupe.util;

/**
 * The Stack is a Last-In-First-Out data structure.
 *
 * @param <T> The type of elements this Stack should contain.
 */
public class Stack<T>
{

    private LinkedNode root;
    private int size;

    /**
     * Constructor of stack. Sets the size to 0 and returns.
     */
    public Stack()
    {
        size = 0;
    }

    /**
     * Puts an element on top of the Stack.
     *
     * @param value The element to put in.
     */
    public void push(T value)
    {
        root = new LinkedNode(value, root);
        size++;
    }

    /**
     * Peek at the top element, without removing it.
     *
     * @return The top element.
     */
    public T peek()
    {
        return root.value;
    }

    /**
     * Removes the top element and returns it to the caller.
     *
     * @return The top element.
     */
    public T pop()
    {
        if (size == 0) {
            throw new IndexOutOfBoundsException("No more elements!");
        }
        T value = root.value;
        root = root.next;
        size--;
        return value;
    }

    /**
     * States whether the Stack is empty or not.
     *
     * @return True if it is empty. False otherwise.
     */
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * Returns the number of elements in the Stack.
     *
     * @return The number of elements in the Stack.
     */
    public int size()
    {
        return size;
    }

    private class LinkedNode
    {

        private T value;
        private LinkedNode next;

        LinkedNode(T value, LinkedNode next)
        {
            this.value = value;
            this.next = next;
        }
    }
}
