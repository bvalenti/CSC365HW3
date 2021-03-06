/*
This class is a custom implemented persistently stored B-Tree.  The class has the singleton form which can be obtained
with the getInstance method. The B-Tree storage location can be set with the BTreePath variable.

String RootIDPath: Indicates the storage location for root node ID for traversals. The root ID can be obtained with
readRootID() which returns a file pointer (long). Multiplying the pointer by the buffer size for each B-Tree node will
yield the location in the file for the start of a given node.
*/

import org.jsoup.Jsoup;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

public class BTree {
    static String BTreePath = "C:\\CSC365HW3_BTree\\btree.ser";
    static String RootIDPath = "C:\\CSC365HW3_BTree\\rootID.ser";
    static String Path = "C:\\CSC365HW3_BTree\\";
    static int t = 4;
    static int childNum = 2 * t;
    static int nodeCount;
    static BNode root = null;

//    private static BTree instance;
//    static {
//        try {
//            instance = new BTree(8,true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public BTree(int k, boolean write) throws IOException {
        BNode x = new BNode(k);
        x.id = 1;
        x.isLeaf = 1;
        x.numOfCurrentKeys = 0;
        root = x;
        nodeCount = 1;
        if (write) {
            x.writeNode();
        }
    }

    //======================================================
    public static NodeIndexPair search(BNode x, String key) throws IOException { //O(t logt n)
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

    //======================================================
    public static Boolean searchReturnBool(BNode x, String key) throws IOException { //O(t logt n)
        int i = 0;
        while (i < x.numOfCurrentKeys && (key.compareTo(x.keys[i]) > 0)) { //key > x.keys
            ++i;
        }
        if (i < x.numOfCurrentKeys && key.equals(x.keys[i])) {
            return true;
        } else if (x.isLeaf == 1) {
            return false;
        } else {
            BNode p = new BNode(childNum);
            p.readNode(x.children[i]);
            return searchReturnBool(p, key);
        }
    }

    //======================================================
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

    public static void printTree(BNode r) throws IOException {
        BNode e;
        for (int i = 0; i < r.children.length; i++) {
            if (r.children[i] != 0) {
                e = new BNode(8);
                e.readNode(r.children[i]);
                printTree(e);
            }
        }
        for (int i = 0; i < r.keys.length; i++) {
            if (r.keys[i] != null) {
                System.out.println(r.keys[i]);
            }
        }
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

//    public static BTree getInstance() { return instance; }

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

    //Method that checks each url in the B-Tree for an associated website. If the webpage is no longer available, the
    //outdated url is replaced with a url whose parent is a randomly chosen url contained in the B-Tree.
    public static void validateAndUpdateUrls(BNode r) throws IOException {
        BNode e;
        MyURLs urls = new MyURLs(false);
        MyUtility utl = new MyUtility();
        HTMLParser parser = new HTMLParser();
        FrequencyTable tmp;

        for (int i = 0; i < r.children.length; i++) {
            if (r.children[i] != 0) {
                e = new BNode(8);
                e.readNode(r.children[i]);
                validateAndUpdateUrls(e);
            }
        }

        for (int i = 0; i < r.keys.length; i++) {
            if (r.keys[i] != null) {
                try {
                    Jsoup.connect(r.keys[i]).get();
                    System.out.println(r.keys[i]);
                } catch (IOException excpt) {
                    System.out.println("Caught exception for url: " + r.keys[i]);
                    int a = ThreadLocalRandom.current().nextInt(0,r.numOfCurrentKeys-1);
                    while (a == i) {
                        a = ThreadLocalRandom.current().nextInt(0,r.numOfCurrentKeys-1);
                    }
                    urls.scrapeForURLS(r.keys[a],1);
                    r.keys[i] = urls.scrapedURLS.get(0).url;
                    r.writeNode();

                    //Delete the old file.
                    java.nio.file.Path path = Paths.get(Path + r.keys[i]);
                    Files.delete(path);

                    Path filePath = utl.getFilePath(r.keys[a]);
                    File file = filePath.toFile();

                    FileOutputStream f = new FileOutputStream(file);
                    ObjectOutputStream os = new ObjectOutputStream(f);
                    tmp = parser.parseURL(urls.scrapedURLS.get(i).url);
                    tmp.frequencies.writeObject(os);
                    os.close();
                    f.close();
                }
            }
        }
    }
}
