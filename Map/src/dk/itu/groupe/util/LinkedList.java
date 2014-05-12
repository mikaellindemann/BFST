package dk.itu.groupe.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedList<T> implements Iterable<T>
{

    private LinkedNode first;
    private LinkedNode last;
    private int size;

    public LinkedList()
    {
        size = 0;
    }
    
    public T removeFirst()
    {
        T value = first.value;
        first = first.next;
        size--;
        return value;
    }

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
    
    public boolean isEmpty()
    {
        return size == 0;
    }
    
    public int size()
    {
        return size;
    }
    
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
    
    public T remove(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        LinkedNode element = first;
        LinkedNode elementBefore = null;
        for (int i = 0; i < index; i++) {
            elementBefore = element;
            element = element.next;
        }
        if (elementBefore != null) {
            elementBefore.next = element.next;
        }
        T value = element.value;
        size--;
        return value;
    }

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

    public T getFirst()
    {
        if (size == 0) {
            throw new IndexOutOfBoundsException();
        }
        return first.value;
    }
    
    public void clear()
    {
        size = 0;
        first = null;
        last = null;
    }

    public T getLast()
    {
        if (size == 0) {
            throw new IndexOutOfBoundsException();
        }
        return last.value;
    }

    @Override
    public Iterator<T> iterator()
    {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T>
    {

        private LinkedNode element;
        private LinkedNode elementBefore;

        private LinkedListIterator()
        {
            element = first;
        }

        @Override
        public boolean hasNext()
        {
            return element != null;
        }

        @Override
        public T next()
        {
            T value = element.value;
            if (value == null) {
                throw new NoSuchElementException();
            }
            elementBefore = element;
            element = element.next;
            return value;
        }
        
        @Override
        public void remove()
        {
            if (elementBefore == null) {
                first = element.next;
            } else {
                elementBefore.next = element.next;
            }
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
