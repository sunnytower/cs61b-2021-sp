package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private T item;
        private Node prev;
        private Node next;

        Node(Node p, T i, Node n) {
            prev = p;
            item = i;
            next = n;
        }

    }
    /** sentinel and size
     *  sentinel == front, sentinel.prev == last. */
    private int size;
    private Node sentinel;

    /** method */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }
    @Override
    public void addFirst(T item) {
        Node toAdd = new Node(sentinel, item, sentinel.next);
        sentinel.next.prev = toAdd;
        sentinel.next = toAdd;
        size++;
    }
    @Override
    public void addLast(T item) {
        Node toAdd = new Node(sentinel.prev, item, sentinel);
        sentinel.prev.next = toAdd;
        sentinel.prev = toAdd;
        size++;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        Node curr = sentinel.next;
        while (curr != sentinel) {
            System.out.print(curr.item + " ");
            curr = curr.next;
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        Node toDelete = sentinel.next;
        T value = toDelete.item;
        sentinel.next = toDelete.next;
        toDelete.next.prev = sentinel;
        toDelete = null;
        size--;
        return value;
    }
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        Node toDelete = sentinel.prev;
        T value = toDelete.item;
        sentinel.prev = toDelete.prev;
        toDelete.prev.next = sentinel;
        toDelete = null;
        size--;
        return value;
    }
    @Override
    public T get(int index) {
        if (size <= index) {
            return  null;
        }
        Node curr = sentinel.next;
        for (int i = 0; i < index; ++i) {
            curr = curr.next;
        }
        return  curr.item;
    }
    private T getRecursiveHelper(Node start, int index) {
        if (index == 0) {
            return start.item;
        }
        return getRecursiveHelper(start.next, index - 1);
    }
    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return getRecursiveHelper(sentinel.next, index);
    }
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return  true;
        }
        if (o instanceof LinkedListDeque) {
            LinkedListDeque<T> test = (LinkedListDeque<T>) o;
            if (test.size() != size) {
                return false;
            }
            for (int i = 0; i < size; ++i) {
                if (test.get(i) != get(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }
    private class LinkedListDequeIterator implements Iterator<T> {
        private Node front;
        LinkedListDequeIterator() {
            front = sentinel.next;
        }

        public boolean hasNext() {
            return front != sentinel;
        }
        public T next() {
            T returnItem = front.item;
            front = front.next;
            return returnItem;
        }
    }
}
