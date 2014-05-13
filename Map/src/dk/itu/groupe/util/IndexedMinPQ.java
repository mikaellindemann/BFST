package dk.itu.groupe.util;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * The Indexed minimum priority queue is used to get indexes in order of their
 * priority.
 *
 * Group E's Map-projects uses this class for shortest and fastest path search,
 * where the indexes are the nodes, and the priorities are the distance from the
 * source, plus the euclidean distance to the target, or the drivetime from the
 * source, if fastest path is chosen.
 *
 * @author Mikael
 * @param <Key> The type that is used to define priorities.
 */
public class IndexedMinPQ<Key extends Comparable<Key>>
{

    Key[] keys; //Used to hold the priorities.
    int[] pq;   //Maps from index in the keys-array to index.
    int[] qp;   //Maps from index to index in the keys-array.
    int size;
    final int maxSize;

    /**
     * Creates a new empty Indexed minimum priority queue with <code>size</code>
     * index places.
     *
     * The priority queue is 0-indexed, which mean indexes should be between 0
     * and <code>size</code> - 1.
     *
     * @param size The number of index-places.
     * @throws IllegalArgumentException If size is less than or equal to 0.
     */
    @SuppressWarnings("unchecked")
    public IndexedMinPQ(int size)
    {
        if (size <= 0) {
            throw new IllegalArgumentException("size can't be less than 0!");
        }
        this.size = 0;
        maxSize = size;
        keys = (Key[]) new Comparable[size];
        pq = new int[size];
        qp = new int[size];
        Arrays.fill(qp, -1);
        Arrays.fill(pq, -1);
    }

    /**
     * States wheter <code>index</code> is in the priority queue.
     *
     * @param index The index to check.
     * @return True if <code>index</code> is in the priority queue. False
     * otherwise.
     */
    public boolean contains(int index)
    {
        if (index < 0 || index >= maxSize) {
            throw new IndexOutOfBoundsException();
        }
        return qp[index] != -1;
    }

    /**
     * Decreases the priority of <code>index</code>.
     *
     * @param index The index to decrease the priority for.
     * @param priority The new priority.
     * @throws IndexOutOfBoundsException If <code>index</code> is less than 0 or
     * greater than or equal to the maximum size of the priority queue.
     *
     * @throws NoSuchElementException If <code>index</code> is not present in
     * the priority queue.
     *
     * @throws IllegalArgumentException If <code>priority</code> is not less
     * than the previous priority.
     */
    public void decreaseKey(int index, Key priority)
    {
        if (index < 0 || index >= maxSize) {
            throw new IndexOutOfBoundsException();
        }
        if (!contains(index)) {
            throw new NoSuchElementException("index is not in the priority queue");
        }
        if (keys[index].compareTo(priority) <= 0) {
            throw new IllegalArgumentException("Calling decreaseKey() with given argument would not decrease the key");
        }
        keys[index] = priority;
        swim(qp[index]);
    }

    /**
     * Delete and return the index with the lowest priority.
     *
     * @return The index with the lowest priority.
     * @throws NoSuchElementException If the priority queue is empty.
     */
    public int delMin()
    {
        if (size == 0) {
            throw new NoSuchElementException("Priority queue is empty!");
        }
        int min = pq[0];
        exch(0, --size);
        sink(0);
        qp[min] = -1;
        keys[pq[size]] = null;
        pq[size] = -1;
        return min;
    }

    /**
     * Associate <code>index</code> with the given <code>priority</code>
     *
     * @param index The index to insert.
     * @param priority The priority of <code>index</code>.
     *
     * @throws IndexOutOfBoundsException If <code>index</code> is less than 0 or
     * greater than or equal to the maximum size of the priority queue.
     *
     * @throws IllegalArgumentException If <code>index</code> is already in the
     * priority queue.
     */
    public void insert(int index, Key priority)
    {
        if (index < 0 || index >= maxSize) {
            throw new IndexOutOfBoundsException();
        }
        if (contains(index)) {
            throw new IllegalArgumentException("index is already in the priority queue!");
        }
        keys[index] = priority;
        qp[index] = size;
        pq[size] = index;
        swim(size++);
    }

    /**
     * States whether this priority queue is empty or not.
     *
     * @return True if the priority queue is empty. False otherwise.
     */
    public boolean isEmpty()
    {
        return size == 0;
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
     * Tells whether the key associated with i is strictly greater than the key
     * associated with j.
     *
     * Greater is used instead of less, because we want a minimum heap (a heap
     * with the lowest element at the first index).
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
     * Sink takes the parameter index and continuously swaps the index with one
     * of its children if its priority is greater than one of the childrens.
     *
     * This is used to rebuild the minimum heap.
     *
     * @param k The index to sink.
     */
    private void sink(int k)
    {
        while (leftChild(k) < size) {
            int j = leftChild(k);
            if (rightChild(k) < size && greater(leftChild(k), rightChild(k))) {
                j = rightChild(k);
            }
            if (!greater(k, j)) {
                break;
            }
            exch(k, j);
            k = j;
        }
    }

    /**
     * Swim takes the parameter index and continuously swaps the index if the
     * priority of the index is less than one of its parents in the heap.
     *
     * This is used to rebuild the minimum heap.
     *
     * @param k The index to swim.
     */
    private void swim(int k)
    {
        while (k > 0 && greater(parent(k), k)) {
            exch(k, parent(k));
            k = parent(k);
        }
    }

    /**
     * Returns the index of the parent of <code>k</code> in the heap.
     *
     * @param k The index to look at.
     * @return The parent of k.
     * @throws IllegalArgumentException If the method is called with index 0.
     */
    private int parent(int k)
    {
        if (k == 0) {
            throw new IllegalArgumentException("The root in a heap has no parent!");
        }
        return (k - 1) / 2;
    }

    /**
     * Returns the index of the left child of k in the heap.
     *
     * It is up to the caller to decide whether the returned index is within the
     * heap.
     *
     * @param k The index to look at.
     * @return The left child of k.
     */
    private int leftChild(int k)
    {
        return k * 2 + 1;
    }

    /**
     * Returns the index of the right child of k in the heap.
     *
     * It is up to the caller to decide whether the returned index is within the
     * heap.
     *
     * @param k The index to look at.
     * @return The right child of k.
     */
    private int rightChild(int k)
    {
        return k * 2 + 2;
    }
}
