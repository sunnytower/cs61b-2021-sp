package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int length;
    private int nextFirst;
    private int nextLast;
    public ArrayDeque() {
        items = (T []) new Object[8];
        /** for convenience, the actual length == length -1, nextfirst == nextlast. */
        length = 8;
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }
    @Override
    public void addFirst(T item) {
        if (size == length - 1) {
            grow();
            items[nextFirst] = item;
            nextFirst = minusOne(nextFirst);
            ++size;
        } else {
            items[nextFirst] = item;
            nextFirst = minusOne(nextFirst);
            ++size;
        }
    }
    @Override
    public void addLast(T item) {
        if (size == length - 1) {
            grow();
            items[nextLast] = item;
            nextLast = plusOne(nextLast, length);
            ++size;
        } else {
            items[nextLast] = item;
            nextLast = plusOne(nextLast, length);
            ++size;
        }
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        if (size == 0) {
            return;
        }
        int front = plusOne(nextFirst, length);
        while (front != nextLast) {
            System.out.print(items[front] + " ");
            front = plusOne(front, length);
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        nextFirst = plusOne(nextFirst, length);
        T ret = items[nextFirst];
        size--;
        if (length >= 16 && timeToResize()) {
            shrink();
        }
        return ret;
    }
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        nextLast = minusOne(nextLast);
        T ret = items[nextLast];
        size--;
        if (length >= 16 && timeToResize()) {
            shrink();
        }
        return ret;
    }
    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int front = plusOne(nextFirst, length);
        for (int i = 0; i < index; i++) {
            front = plusOne(front, length);
        }
        return items[front];
    }
    private boolean timeToResize() {
        return (length / size) >= 4;
    }

    private void grow() {
        int newLength = length * 2;
        T[] newItems = (T []) new Object[newLength];
        int originFront = plusOne(nextFirst, length);
        int newFront = length;
        while (originFront != nextLast) {
            newItems[newFront] = items[originFront];
            items[originFront] = null;
            originFront = plusOne(originFront, length);
            newFront = plusOne(newFront, newLength);
        }
        nextFirst = length - 1;
        nextLast = newFront;
        length = newLength;
        items = newItems;
    }
    private void shrink() {
        int newLength = length / 2;
        T[] newItems = (T []) new Object[newLength];
        int originFront = plusOne(nextFirst, length);
        int newFront = newLength / 2;
        while (originFront != nextLast) {
            newItems[newFront] = items[originFront];
            items[originFront] = null;
            originFront = plusOne(originFront, length);
            newFront = plusOne(newFront, newLength);
        }
        nextFirst = newLength / 2 - 1;
        nextLast = newFront;
        length = newLength;
        items = newItems;
    }
    private int minusOne(int index) {
        if (index == 0) {
            return length - 1;
        }
        return index - 1;
    }
    private int plusOne(int index, int l) {
        if (index + 1 == l) {
            return 0;
        }
        return index + 1;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return  true;
        }
        if (o instanceof Deque) {
            Deque<?> test = (Deque<?>) o;
            if (test.size() != size) {
                return false;
            }
            for (int i = 0; i < size; ++i) {
                if (!test.get(i).equals(get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;
        ArrayDequeIterator() {
            pos = 0;
        }

        public boolean hasNext() {
            return pos < size;
        }
        public T next() {
            T returnItem = get(pos);
            pos += 1;
            return returnItem;
        }
    }
}
