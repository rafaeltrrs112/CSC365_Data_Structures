/**
 * BTree from the algorithms book.
 *
 * @param <Key>
 * @param <Value>
 */
public class BTree<Key extends Comparable<Key>, Value> {
    // max children per B-tree node = M-1
    // (must be even and greater than 2)
    private static final int M = 4;

    private Node root;       // root of the B-tree
    private int height;      // height of the B-tree
    private int N;           // number of key-value pairs in the B-tree

    // helper B-tree node data type
    private static final class Node {
        private int childCount;                             // number of children
        private Entry[] children = new Entry[M];   // the array of children

        // create a node with k children
        private Node(int k) {
            childCount = k;
        }
    }

    // internal nodes: only use key and next
    // external nodes: only use key and value
    private static class Entry {
        private Comparable key;
        private Object val;
        private Node next;     // helper field to iterate over array entries

        public Entry(Comparable key, Object val, Node next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }

    /**
     * Initializes an empty B-tree.
     */
    public BTree() {
        root = new Node(0);
    }

    /**
     * Returns true if this symbol table is empty.
     *
     * @return <tt>true</tt> if this symbol table is empty; <tt>false</tt> otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     *
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return N;
    }

    /**
     * Returns the height of this B-tree (for debugging).
     *
     * @return the height of this B-tree
     */
    public int height() {
        return height;
    }


    /**
     * Returns the value associated with the given key.
     *
     * @param key the key
     * @return the value associated with the given key if the key is in the symbol table
     * and <tt>null</tt> if the key is not in the symbol table
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public Value get(Key key) {
        if (key == null) throw new NullPointerException("key must not be null");
        return search(root, key, height);
    }

    private Value search(Node x, Key key, int ht) {
        Entry[] children = x.children;

        // external node
        if (ht == 0) {
            for (int j = 0; j < x.childCount; j++) {
                if (eq(key, children[j].key)) return (Value) children[j].val;
            }
        }

        // internal node
        else {
            for (int j = 0; j < x.childCount; j++) {
                if (j + 1 == x.childCount || less(key, children[j + 1].key))
                    return search(children[j].next, key, ht - 1);
            }
        }
        return null;
    }


    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is <tt>null</tt>, this effectively deletes the key from the symbol table.
     *
     * @param key the key
     * @param val the value
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public void put(Key key, Value val) {
        if (key == null) throw new NullPointerException("key must not be null");
        Node u = insert(root, key, val, height);
        N++;
        if (u == null) return;

        // need to split root
        Node t = new Node(2);
        t.children[0] = new Entry(root.children[0].key, null, root);
        t.children[1] = new Entry(u.children[0].key, null, u);
        root = t;
        height++;
    }

    /**
     * Inserts a key value pair starting at node @param h.
     *
     * @param h   The node to being insertion at.
     * @param key The key being inserted.
     * @param val The value being inserted.
     * @param ht  The current number of steps until a leaf is reached. if > 0, not leaf, else leaf. ht != -1.
     * @return
     *      Returns null if a split is not necessary. Else the center node is returned.
     */
    private Node insert(Node h, Key key, Value val, int ht) {
        int j; // The child array index to place the new entry into.
        Entry t = new Entry(key, val, null); // Create an entry with key value pairs and no next value.

        // If the height has been exhausted then we are at a leaf. Start traversing the array of children
        // to find the position in the node place the new entry at.
        if (ht == 0) {
            for (j = 0; j < h.childCount; j++) {
                if (less(key, h.children[j].key)) break;
            }
        }

        // If the height has not been exhausted then continue traversing through the tree.
        else {
            for (j = 0; j < h.childCount; j++) {
                if ((j + 1 == h.childCount) || less(key, h.children[j + 1].key)) { //
                    Node u = insert(h.children[j++].next, key, val, ht - 1);
                    if (u == null) return null;
                    t.key = u.children[0].key;
                    t.next = u;
                    break;
                }
            }
        }

        for (int i = h.childCount; i > j; i--)
            h.children[i] = h.children[i - 1];
        h.children[j] = t;
        h.childCount++;
        if (h.childCount < M) return null;
        else return split(h);
    }

    // split node in half
    private Node split(Node h) {
        Node t = new Node(M / 2);
        h.childCount = M / 2;
        for (int j = 0; j < M / 2; j++)
            t.children[j] = h.children[M / 2 + j];
        return t;
    }

    /**
     * Returns a string representation of this B-tree (for debugging).
     *
     * @return a string representation of this B-tree.
     */
    public String toString() {
        return toString(root, height, "") + "\n";
    }

    private String toString(Node h, int ht, String indent) {
        StringBuilder s = new StringBuilder();
        Entry[] children = h.children;

        if (ht == 0) {
            for (int j = 0; j < h.childCount; j++) {
                s.append(indent + children[j].key + " " + children[j].val + "\n");
            }
        } else {
            for (int j = 0; j < h.childCount; j++) {
                if (j > 0) s.append(indent + "(" + children[j].key + ")\n");
                s.append(toString(children[j].next, ht - 1, indent + "     "));
            }
        }
        return s.toString();
    }

    /**
     * Determines whether k1 is smaller than k2.
     * @param k1
     *          The first key.
     * @param k2
     *          The second key.
     * @return
     *         True if k1 is smaller than k2.
     */
    private boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }

    /**
     * Determines whether k1 is equal to k2.
     * @param k1
     *          The first key.
     * @param k2
     *          The second key.
     * @return
     *         True if k1 is equal to k2.
     */
    private boolean eq(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }


}