package hashmap;


import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double MAX_LF = 0.75;
    private double maxLoad;
    private int size;
    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE, MAX_LF);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, MAX_LF);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        this.maxLoad = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] newTable = new Collection[tableSize];
        for (int i = 0; i < tableSize; ++i) {
            newTable[i] = createBucket();
        }
        return newTable;
    }

    // Your code won't compile until you do so!
    @Override
    public void clear() {
        size = 0;
        buckets = createTable(DEFAULT_INITIAL_SIZE);
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public boolean containsKey(K key) {
        return getNode(key) != null;
    }
    @Override
    public V get(K key) {
        Node n = getNode(key);
        if (n == null) {
            return null;
        }
        return n.value;
    }
    @Override
    public void put(K key, V value) {
        if (needToResize()) {
            resize(buckets.length * 2);
        }
        if (containsKey(key)) {
            Node n = getNode(key);
            n.value = value;
            return;
        }
        Node n = createNode(key, value);
        int bucketIndex = getBucketIndex(key);
        buckets[bucketIndex].add(n);
        size++;
        return;
    }
    @Override
    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        addKeys(set);
        return set;
    }
    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }
    private void addKeys(Set<K> set) {
        for (Collection<Node> bucket : buckets) {
            for (Node n : bucket) {
                if (n != null) {
                    set.add(n.key);
                }
            }
        }
        return;
    }

    private Node getNode(K key) {
        return getNode(key, getBucketIndex(key));
    }
    private Node getNode(K key, int bucketIndex) {
        if (buckets[bucketIndex] == null) {
            return null;
        }
        for (Node n : buckets[bucketIndex]) {
            if (n.key.equals(key)) {
                return n;
            }
        }
        return null;
    }
    private boolean needToResize() {
        return (double) (size / buckets.length) > maxLoad;
    }
    private void resize(int newSize) {
        Collection<Node>[] newBuckets = createTable(newSize);
        for (Collection<Node> bucket : buckets) {
            for (Node n : bucket) {
                int bucketIndex = getBucketIndex(n.key);
                newBuckets[bucketIndex].add(n);
            }
        }
        buckets = newBuckets;
    }
    private int getBucketIndex(K key) {
        return Math.floorMod(key.hashCode(), buckets.length);
    }
}
