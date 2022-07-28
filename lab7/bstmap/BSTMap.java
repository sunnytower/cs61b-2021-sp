package bstmap;



import java.util.HashSet;
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
        return get(root, key);
    }
    private V get(Node x, K key) {
        if (key == null) throw new IllegalArgumentException("argument to containsKey() is null");
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return get(x.left, key);
        } else if (cmp > 0) {
            return get(x.right, key);
        } else {
            return x.value;
        }
    }
@Override
    public int size() {
        return size(root);
    }
    private int size(Node x) {
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
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }
    public void printInOrder() {
        printInOrder(root);
    }
    private void printInOrder(Node x) {
        if (x == null) {
            return;
        }
        printInOrder(x.left);
        System.out.println(x.key + "->" + x.value);
        printInOrder(x.right);
    }
    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        addKeys(root, set);
        return set;
    }
    private void addKeys(Node x, Set<K> set) {
        if (x == null) {
            return;
        }
        set.add(x.key);
        addKeys(x.left, set);
        addKeys(x.right, set);
    }
@Override
    public V remove(K key) {
        if(containsKey(key)) {
            V target = get(key);
            root = remove(root, key);
            return target;
        }
        return null;
    }
    private Node remove(Node x, K key) {
        if (x == null) {
            return null;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = remove(x.left, key);
        } else if (cmp > 0) {
            x.right = remove(x.right, key);
        } else {
            if (x.right == null) {
                return x.left;
            }
            if (x.left == null) {
                return x.right;
            }
            Node t = x;
            x = getMin(t.right);
            x.left = t.left;
            x.right = remove(t.right, x.key);
        }
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }
    private Node getMin(Node x) {
        if (x.left == null) {
            return x;
        } else {
            return getMin(x.left);
        }
    }

@Override
    public V remove(K key, V value) {
        if (containsKey(key)) {
            V target = get(key);
            if (target.equals(value)) {
                root = remove(root, key);
                return target;
            }
        }
        return null;
    }
@Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

}
