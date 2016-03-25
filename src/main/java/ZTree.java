/**
 * ZTree from the algorithms book.
 *
 * @param <Key>
 * @param <Value>
 */
public class ZTree<Key extends Comparable<Key>, Value> {
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

        Entry(Comparable key, Object val, Node next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }

    /**
     * Initializes an empty B-tree.
     */
    public ZTree() {
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


    /**
     * Searches for a key starting at a give node and height recursively.
     *
     * @param x   The node to start searching at.
     * @param key The key being searched for.
     * @param ht  The current height level of the recursive search.
     * @return The value associated with the given key if any.
     */
    private Value search(Node x, Key key, int ht) {
        Entry[] children = x.children;

        // external node
        if (ht == 0) {
            for (int j = 0; j < x.childCount; j++) {
                if (eq(key, children[j].key)) {
                    System.out.println("Hit at height " + ht);
                    return (Value) children[j].val;
                }
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
     * @return Returns null if a split is not necessary. Else returns the right node (contains the larger RIGHT half of values).
     */
    private Node insert(Node h, Key key, Value val, int ht) {
        int j; // The child array index to place the new entry into.
        Entry t = new Entry(key, val, null); // Create an entry with key value pairs and no next value.

        /* If the height has been exhausted then we are at a leaf. Start traversing the array of children
         * to find the position in the node to place the new entry at.
         */
        if (ht == 0) {
            for (j = 0; j < h.childCount; j++) {
                if (less(key, h.children[j].key)) {
                    break;
                }
            }
        }

        /* If the height has not been exhausted then continue traversing through the tree.
         *
         * Traversal :
         *
         * NOTE : internal nodes: only use key and next
         *        external nodes: only use key and value
         */
        else {
            //For loop locates the correct child node to drop into to continue traversal.
            for (j = 0; j < h.childCount; j++) {
                if ((j + 1 == h.childCount) || less(key, h.children[j + 1].key)) { //True if loop is at last child's index (j), or the key for the current entry fits into the current slot (j).
//                    System.out.println(h.children[j++].next);
                    Node u = insert(h.children[j++].next, key, val, ht - 1); // Recursively call insert with the h(drop into node) being an intermediate key'd valued child or the last child if key is larger than all.
                                                //J is incremented here because the if statement is hit if the key is larger than all child links or a link larger than the key is found. If a node is promoted it
                                                //may be greater than or equal to the node that was slotted into here but not greater than node J + 1. So node j + 1 will be replaced with the promoted node and it will
                                                //be shifted to the right because it is guaranteed to be greater than the promoted node.
                    if (u == null) return null; // If the insertion does not result in a split then return null
                    t.key = u.children[0].key; // If the node is split set the T node's key to the key of the new RIGHT NODE.
                                                                //This updates the key copy links in this node for the future.
                    t.next = u; //The next value of T is set as the new RIGHT NODE
                    break;
                }
            }
        }

        System.out.println(j);

        /*
         * Start iterating at the number of children the current node has.
         * Stop once i is less than the position the new entry is being placed into.
         *
         * Element in t's place in h and elements where [i] > t's [i] are all moved to the right.
         *
         */
        for (int i = h.childCount; i > j; i--)
            h.children[i] = h.children[i - 1];

        //Drop new entry node into it's target location.
        h.children[j] = t;
        h.childCount++;
        return (h.childCount < M) ? null : split(h); // Return null if the number of children is not at the degree count.
    }

    // split node in half then return node RIGHT (contains the largest valued half of the nodes).
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
                s.append(indent).append(children[j].key).append(" ").append(children[j].val).append(", at height ").append(ht).append("\n");
            }
        } else {
            for (int j = 0; j < h.childCount; j++) {
                if (j > 0) s.append(indent).append("(").append(children[j].key).append(")\n");
                s.append(toString(children[j].next, ht - 1, indent + "     "));
            }
        }
        return s.toString();
    }

    /**
     * Determines whether k1 is smaller than k2.
     *
     * @param k1 The first key.
     * @param k2 The second key.
     * @return True if k1 is smaller than k2.
     */
    private boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }

    /**
     * Determines whether k1 is equal to k2.
     *
     * @param k1 The first key.
     * @param k2 The second key.
     * @return True if k1 is equal to k2.
     */
    private boolean eq(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }

    /**
     * Unit tests the <tt>ZTree</tt> data type.
     */
    public static void main(String[] args) {
        ZTree<String, String> st = new ZTree<>();

        st.put("www.cs.princeton.edu", "128.112.136.12");
        st.put("www.cs.princeton.edu", "128.112.136.11");
        st.put("www.princeton.edu", "128.112.128.15");
        st.put("www.yale.edu", "130.132.143.21");
        st.put("www.simpsons.com", "209.052.165.60");
        st.put("www.epple.com", "17.112.152.32");
        st.put("www.emazon.com", "207.171.182.16");
        st.put("www.ebay.com", "66.135.192.87");
        st.put("www.cnn.com", "64.236.16.20");
        st.put("www.google.com", "216.239.41.99");
        st.put("www.nytimes.com", "199.239.136.200");
        st.put("www.microsoft.com", "207.126.99.140");
        st.put("www.dell.com", "143.166.224.230");
        st.put("www.slashdot.org", "66.35.250.151");
        st.put("www.espn.com", "199.181.135.201");
        st.put("www.weather.com", "63.111.66.11");
        st.put("www.yahoo.com", "216.109.118.65");

//
        System.out.println("cs.princeton.edu:  " + st.get("www.cs.princeton.edu"));
        System.out.println("hardvardsucks.com: " + st.get("www.harvardsucks.com"));
        System.out.println("simpsons.com:      " + st.get("www.simpsons.com"));
        System.out.println("epple.com:         " + st.get("www.apple.com"));
        System.out.println("ebay.com:          " + st.get("www.ebay.com"));
        System.out.println("dell.com:          " + st.get("www.dell.com"));
        System.out.println("yahoo.com          " + st.get("www.yahoo.com"));
//
//        System.out.println("size:    " + st.size());
        System.out.println("height:  " + st.height());
//        System.out.println(st);
//        System.out.println();
    }


}