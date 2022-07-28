package bstmap;



import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private Node root;
    private class Node {
        private K key;
        private V value;
        private Node left;
        private Node right;
        private int size;
        Node(K k, V v, int s) {
            key = k;
            value = v;
            size = s;
        }
    }
    public BSTMap() {
        root = null;
    }
@Override
    public void clear() {
        root = null;
    }
@Override
    public boolean containsKey(K key) {
        if (key == null) throw new IllegalArgumentException("argument to containsKey() is null");
        return findKey(root, key);
    }
    private boolean findKey(Node x,K key) {
        if (key == null) throw new IllegalArgumentException("argument to containsKey() is null");
        if (x == null) return false;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return findKey(x.left, key);
        } else if (cmp > 0) {
            return findKey(x.right, key);
        } else {
            return true;
        }
    }
@Override
    public V get(K key) {
        return getHelper(root, key);
    }
    private V getHelper(Node x, K key) {
        if (key == null) throw new IllegalArgumentException("argument to containsKey() is null");
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return getHelper(x.left, key);
        } else if (cmp > 0) {
            return getHelper(x.right, key);
        } else {
            return x.value;
        }
    }
@Override
    public int size() {
        return sizeHelper(root);
    }
    private int sizeHelper(Node x) {
        if (x == null) {
            return 0;
        }
        return x.size;
    }
@Override
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("calls put() with a null key.");
        root = put(root, key, value);
    }
    private Node put(Node x, K key, V val) {
        if (x == null) {
            return new Node(key, val, 1);
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = put(x.left, key, val);
        } else if (cmp > 0) {
            x.right = put(x.right, key, val);
        } else {
            x.value = val;
        }
        x.size = 1 + sizeHelper(x.left) + sizeHelper(x.right);
        return x;
    }
    public void PrintInOrder() {

    }
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("invalid operation.");
    }
@Override
    public V remove(K key) {
        throw new UnsupportedOperationException("invalid operation.");
    }
@Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("invalid operation.");
    }
@Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("invalid operation.");
    }

}
