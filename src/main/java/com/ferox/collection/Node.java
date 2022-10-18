package com.ferox.collection;

public class Node {

    public long key;
    public Node prev;
    public Node next;

    public final void remove() {
        if (next != null) {
            next.prev = prev;
            prev.next = next;
            prev = null;
            next = null;
        }
    }

    public final boolean hasPrevious() {
        if(next == null)
            return false;

        return true;

    }
}
