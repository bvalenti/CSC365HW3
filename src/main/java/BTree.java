/*
This class is a custom implemented persistently stored B-Tree.  The class has the singleton form which can be obtained
with the getInstance method. The B-Tree storage location can be set with the BTreePath variable.

String RootIDPath: Indicates the storage location for root node ID for traversals. The root ID can be obtained with
readRootID() which returns a file pointer (long). Multiplying the pointer by the buffer size for each B-Tree node will
yield the location in the file for the start of a given node.
*/

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BTree {
    static String BTreePath = "C:\\CSC365HW3_BTree\\btree.ser";
    static String RootIDPath = "C:\\CSC365HW3_BTree\\rootID.ser";
    static String Path = "C:\\CSC365HW3_BTree\\";
    static int t = 4;
    static int childNum = 2 * t;
    static int nodeCount;
    static BNode root = null;

    private static BTree instance;
    static {
        try {
            instance = new BTree(8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BTree(int k) throws IOException {
        BNode x = new BNode(k);
        x.id = 1;
        x.isLeaf = 1;
        x.numOfCurrentKeys = 0;
        x.writeNode();
        root = x;
        nodeCount = 1;
    }

    public NodeIndexPair search(BNode x, String key) throws IOException { //O(t logt n)
        int i = 0;
        while (i < x.numOfCurrentKeys && (key.compareTo(x.keys[i]) > 0)) { //key > x.keys
            ++i;
        }
        if (i < x.numOfCurrentKeys && key.equals(x.keys[i])) {
            NodeIndexPair out = new NodeIndexPair(x, i);
            return out;
        } else if (x.isLeaf == 1) {
            return null;
        } else {
            BNode p = new BNode(childNum);
            p.readNode(x.children[i]);
            return search(p, key);
        }
    }

    public static void insert(String k) throws IOException {
        BNode r = root;
        if (r.numOfCurrentKeys == 2*t-1) {
            BNode s = new BNode(childNum);
            root = s;
            s.isLeaf = 0;
            s.numOfCurrentKeys = 0;
            s.children[0] = r.id;
            nodeCount++;
            s.id = nodeCount;
            cacheRootID(s.id);
            r.parent = s.id;
            r.writeNode();
            splitChild(s,0);
            insertNonFull(s,k);
        } else {
            insertNonFull(r,k);
        }
    }

    public static void insertNonFull(BNode x, String k) throws IOException {
        int index = x.numOfCurrentKeys-1;
        if (x.isLeaf == 1) {
            while (index >= 0 && (k.compareTo(x.keys[index]) < 0)) { //k < x.keys
                x.keys[index+1] = x.keys[index];
                index--;
            }
            x.keys[index+1] = k;
            x.numOfCurrentKeys++;
            x.writeNode();
        } else {
            while (index >= 0 && (k.compareTo(x.keys[index]) < 0)) {  //k < x.keys
                index--;
            }
            index++;
            BNode c = new BNode(childNum);
            c.readNode(x.children[index]);
            if (c.numOfCurrentKeys == 2*t - 1) {
                splitChild(x, index);
                if (k.compareTo(x.keys[index]) > 0) {  //k > x.keys
                    index++;
                }
            }
            c = new BNode(childNum);
            c.readNode(x.children[index]);
            insertNonFull(c, k);
        }
    }

    public static void splitChild(BNode x, int index) throws IOException {
        BNode z = new BNode(childNum);
        BNode y = new BNode(childNum);
        BNode tmp;
        y.readNode(x.children[index]);
        y.parent = x.id;
        z.isLeaf = y.isLeaf;
        nodeCount++;
        z.id = nodeCount;
        z.parent = x.id;

        z.numOfCurrentKeys = t - 1;
        for (int j = 1; j <= t - 1; j++) {
            z.keys[j-1] = y.keys[j+t-1];
            y.keys[j+t-1] = null;
        }

        if (!(y.isLeaf == 1)) {
            for (int j = 1; j <= t; j++) {
                z.children[j-1] = y.children[j+t-1];
                tmp = new BNode(childNum);
                tmp.readNode(z.children[j-1]);
                tmp.parent = z.id;
                tmp.writeNode();
                y.children[j+t-1] = 0;
            }
        }
        y.numOfCurrentKeys = t - 1;
        for (int j = x.numOfCurrentKeys; j >= index+1; j--) {
            x.children[j+1] = x.children[j];
        }
        x.children[index+1] = z.id;
        for (int j = x.numOfCurrentKeys-1; j >= index; j--) {
            x.keys[j+1] = x.keys[j];
        }
        x.keys[index] = y.keys[t-1];
        y.keys[t-1] = null;

        x.numOfCurrentKeys++;
        y.writeNode();
        z.writeNode();
        x.writeNode();
    }

    public static void cacheRootID(long rootID) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(BTree.RootIDPath, "rw");
        FileChannel ff = raf.getChannel();
        ByteBuffer b = ByteBuffer.allocate(8);
        b.putLong(rootID);
        b.flip();
        while(b.hasRemaining()) {
            ff.write(b);
        }
        ff.close();
        raf.close();
    }

    public static BTree getInstance() { return instance; }

    public static long readRootId() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(BTree.RootIDPath, "rw");
        FileChannel ff = raf.getChannel();
        ByteBuffer b = ByteBuffer.allocate(8);
        ff.read(b);
        b.position(0);
        long out = b.getLong();
        ff.close();
        raf.close();
        return out;
    }
}
