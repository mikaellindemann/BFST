package dk.itu.groupe.util;

import java.util.NoSuchElementException;

/**
 * Represents a heap-ordered indexed minimum priority queue.
 *
 * @author Mikael, Peter, Rune.
 * @param <Key> The type that defines how the indexes are ordered.
 */
public class IndexMinimumPriorityQueue<Key extends Comparable<Key>>
{

    private int numberOfIndexes;        // maximum number of elements on PQ
    private int size;           // number of elements on PQ
    private int[] pq;        // binary heap using 1-based indexing
    private int[] qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private Key[] keys;      // keys[i] = priority of i

    /**
     * Creates a new Indexed minimum priority queue
     *
     * @param numberOfIndexes The number of indexes this queue should hold.
     */
    @SuppressWarnings("unchecked")
    public IndexMinimumPriorityQueue(int numberOfIndexes)
    {
        if (numberOfIndexes < 0) {
            throw new IllegalArgumentException();
        }
        this.numberOfIndexes = numberOfIndexes;
        keys = (Key[]) new Comparable[numberOfIndexes + 1];
        pq = new int[numberOfIndexes + 1];
        qp = new int[numberOfIndexes + 1];
        for (int i = 0; i <= numberOfIndexes; i++) {
            qp[i] = -1;
        }
    }

    /**
     * States whether the Priority Queue is empty or not.
     *
     * @return true if the priority queue is empty. false otherwise.
     */
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * Is i an index on the priority queue?
     *
     * @param i an index
     * @return true if the queue has a value asociated with this index. false
     * otherwise.
     * @throws java.lang.IndexOutOfBoundsException unless (0 &le; i < NMAX)
     */
    public boolean contains(int i)
    {
        if (i < 0 || i >= numberOfIndexes) {
            throw new IndexOutOfBoundsException();
        }
        return qp[i] != -1;
    }

    /**
     * Associates key with index i.
     *
     * @param i an index
     * @param key the key to associate with index i
     * @throws java.lang.IndexOutOfBoundsException if the index is less than 0
     * or greater than <pre>numberOfIndexes</pre>.
     *
     * @throws java.lang.IllegalArgumentException if there already is an item
     * associated with index i
     */
    public void insert(int i, Key key)
    {
        if (i < 0 || i >= numberOfIndexes) {
            throw new IndexOutOfBoundsException();
        }
        if (contains(i)) {
            throw new IllegalArgumentException("index is already in the priority queue");
        }
        size++;
        qp[i] = size;
        pq[size] = i;
        keys[i] = key;
        swim(size);
    }

    /**
     * Removes a minimum key and returns its associated index.
     *
     * @return an index associated with a minimum key
     * @throws java.util.NoSuchElementException if priority queue is empty
     */
    public int delMin()
    {
        if (size == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        int min = pq[1];
        exch(1, size--);
        sink(1);
        qp[min] = -1;
        keys[pq[size + 1]] = null;
        pq[size + 1] = -1;
        return min;
    }

    /**
     * Decrease the key associated with index i to the specified value.
     *
     * @param i the index of the key to decrease
     * @param key decrease the key assocated with index i to this key
     * @throws java.lang.IndexOutOfBoundsException if i is less than 0, or
     * greater than or equal to numberOfIndexes
     * @throws java.lang.IllegalArgumentException If key is greater than or
     * equal to the key currently associated with i.
     * @throws java.util.NoSuchElementException no key is associated with index
     * i
     */
    public void decreaseKey(int i, Key key)
    {
        if (i < 0 || i >= numberOfIndexes) {
            throw new IndexOutOfBoundsException();
        }
        if (!contains(i)) {
            throw new NoSuchElementException("index is not in the priority queue");
        }
        if (keys[i].compareTo(key) <= 0) {
            throw new IllegalArgumentException("Calling decreaseKey() with given argument would not strictly decrease the key");
        }
        keys[i] = key;
        swim(qp[i]);
    }

    /**
     * Tells whether the key associated with i is strictly greater than the key
     * associated with j.
     *
     * @param i an index
     * @param j another index
     * @return true if the key associated with i is greater than the key
     * associated with j. false otherwise.
     */
    private boolean greater(int i, int j)
    {
        return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
    }

    /**
     * Swaps the values of i and j in pq and qp.
     *
     * @param i
     * @param j
     */
    private void exch(int i, int j)
    {
        int swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
        qp[pq[i]] = i;
        qp[pq[j]] = j;
    }

    /**
     * Heap-helpermethod.
     *
     * @param k
     */
    private void swim(int k)
    {
        while (k > 1 && greater(k / 2, k)) {
            exch(k, k / 2);
            k = k / 2;
        }
    }

    /**
     * Heap-helpermethod.
     *
     * @param k
     */
    private void sink(int k)
    {
        while (2 * k <= size) {
            int j = 2 * k;
            if (j < size && greater(j, j + 1)) {
                j++;
            }
            if (!greater(k, j)) {
                break;
            }
            exch(k, j);
            k = j;
        }
    }
}
