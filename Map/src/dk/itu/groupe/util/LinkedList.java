package dk.itu.groupe.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The LinkedList is a datastructure that can hold an arbitrary number of
 * elements.
 *
 * As the list is not backed by an array, no time is used to resize such one. A
 * LinkedList uses internal objects to keep track of the list instead.
 *
 * This implementation of a LinkedList only supports the features used in Group
 * E's Map-project. This means that for instance once an element has been added
 * to the list, it cannot be removed.
 *
 * @author Mikael
 * @param <T> The type of elements this LinkedList should contain.
 */
public class LinkedList<T> implements Iterable<T>
{

    private LinkedNode first;
    private LinkedNode last;
    private int size;

    /**
     * Creates a new empty LinkedList.
     */
    public LinkedList()
    {
        size = 0;
    }

    /**
     * Adds value to the list.
     *
     * @param value The value to add.
     */
    public void add(T value)
    {
        if (size == 0) {
            last = first = new LinkedNode(value, null);
        } else {
            last.next = new LinkedNode(value, null);
            last = last.next;
        }
        size++;
    }

    /**
     * States whether the list is empty or not.
     *
     * @return True if the list is empty, false otherwise.
     */
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * Returns the size of this list.
     *
     * @return The size of the list.
     */
    public int size()
    {
        return size;
    }

    /**
     * Returns an array of the elements in this list.
     *
     * The elements are ordered in the way they were added to the list. If the
     * List is empty, it returns an empty array.
     *
     * This method uses operations linear in the number of elements in this
     * list.
     *
     * @return An array of the elements in this list.
     */
    public T[] toArray()
    {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) new Object[size];
        LinkedNode element = first;
        for (int i = 0; i < size; i++) {
            array[i] = element.value;
            element = element.next;
        }
        return array;
    }

    /**
     * Returns the element at index.
     *
     * @param index The index of the element. This list is 0-indexed, which
     * means the first element are at index 0.
     * @return The element and <code>index</code>.
     *
     * @throws IndexOutOfBoundsException If index is less than 0 or greater than
     * or equal to the size of the list.
     */
    public T get(int index)
    {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        LinkedNode element = first;
        for (int i = 0; i < index; i++) {
            element = element.next;
        }
        return element.value;
    }

    /**
     * Returns the last element in this list.
     *
     * @return The last element in this list.
     * @throws IndexOutOfBoundsException If the list is empty.
     */
    public T getLast()
    {
        if (size == 0) {
            throw new IndexOutOfBoundsException();
        }
        return last.value;
    }

    /**
     * Returns an Iterator over the elements in this list.
     *
     * The iterator goes through the elements in the same order as they were
     * added to this list.
     *
     * @return An Iterator over the elements in this list.
     */
    @Override
    public Iterator<T> iterator()
    {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T>
    {

        private LinkedNode element;

        private LinkedListIterator()
        {
            element = first;
        }

        /**
         * States whether or not this iterator has more elements.
         *
         * @return True if there are more elements. False otherwise.
         */
        @Override
        public boolean hasNext()
        {
            return element != null;
        }

        /**
         * Returns the next element this iterator points at.
         *
         * @return the next element this iterator points at.
         * @throws NoSuchElementException If this method is called and there are
         * no elements left. hasNext() should be called to check this.
         */
        @Override
        public T next()
        {
            if (element == null) {
                throw new NoSuchElementException();
            }
            T value = element.value;
            element = element.next;
            return value;
        }
    }

    private class LinkedNode
    {

        T value;
        LinkedNode next;

        LinkedNode(T value, LinkedNode next)
        {
            this.value = value;
            this.next = next;
        }
    }
}
