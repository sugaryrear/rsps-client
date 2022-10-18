package com.ferox.collection;

public final class HashTable {

    private final int size;
    private final Node[] cache;

    public HashTable() {
        size = 1024;
        cache = new Node[size];
        for (int index = 0; index < size; index++) {
            Node node = cache[index] = new Node();
            node.prev = node;
            node.next = node;
        }
    }

    public Node get(long key) {
        Node bucket = cache[(int) (key & (long) (size - 1))];
        for (Node node = bucket.prev; node != bucket; node = node.prev)
            if (node.key == key)
                return node;

        return null;
    }

    public void clear() {
        for(int id = 0; id < size; id++) {
            Node link = cache[id];
            for(;;) {
                Node next = link.next;
                if(link == next)
                    break;

                next.remove();
            }
        }
    }

    public void put(Node node, long key) {
        if (node.next != null)
            node.remove();

        Node bucket = cache[(int) (key & (long) (size - 1))];
        node.next = bucket.next;
        node.prev = bucket;
        node.next.prev = node;
        node.prev.next = node;
        node.key = key;
        return;
    }
}
