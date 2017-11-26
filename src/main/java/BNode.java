import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class BNode {
    String BTreePath = "C:\\CSC365HW3_BTree\\btree.ser";
    int size;
    int buffSize = 2284;
    long id;
    long parent;
    int usedForBreadthSearching = 0;
    int count;
    int numOfCurrentKeys;
    int isLeaf;
    String keys[];
    long children[];

    BNode(int k) {
        keys = new String[k-1];
        count = 0;
        children = new long[k];
        size = k;
//        parent = 2;
    }

    public void readNode(long pointer) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(BTreePath, "r");
//        raf.seek(12 + (pointer-1)*buffSize);
        raf.seek((pointer-1)*buffSize);
        FileChannel f = raf.getChannel();
        ByteBuffer b = ByteBuffer.allocate(buffSize);

        f.read(b);
        b.position(0);
        id = b.getLong();
        parent = b.getLong();
        usedForBreadthSearching = b.getInt();
        count = b.getInt();
        numOfCurrentKeys = b.getInt();
        isLeaf = b.getInt();
        for (int i = 0; i < keys.length; i++) {
            int len = b.getInt(); //Read string length
            if (len != 0) {
                byte[] bytes = new byte[len];
                b.get(bytes);
                keys[i] = new String(bytes);
            }
        }
        for (int i = 0; i < size; i++) {
            children[i] = b.getLong();
        }
        f.close();
        raf.close();
    }

    public void writeNode() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(BTreePath, "rw");
//        raf.seek(12 + (id-1)*buffSize);
        raf.seek((id-1)*buffSize);
        FileChannel f = raf.getChannel();
        ByteBuffer b = ByteBuffer.allocate(buffSize);

        b.putLong(id);
        b.putLong(parent);
        b.putInt(usedForBreadthSearching);
        b.putInt(count);
        b.putInt(numOfCurrentKeys);
        b.putInt(isLeaf);
        byte toWrite[];
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null) {
                b.putInt(0);
            } else {
                b.putInt(keys[i].length());
                toWrite = keys[i].getBytes();
                b.put(toWrite);
            }
        }
        for (int i = 0; i < size; i++) {
            b.putLong(children[i]);
        }
        b.flip();
        while(b.hasRemaining()) {
            f.write(b);
        }
        f.close();
        raf.close();
    }
}
