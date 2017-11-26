import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MyUtility {
    int nodeCount;
    long urlPaths[] = new long[1000];
    String MedoidsPath = "C:\\CSC365HW3_BTree\\medoids.ser";
    int numberOfMedoids = 10;
    double sim = 0;
    long lastModified = Long.MAX_VALUE;
    String fileKeyLastModified = "";
    File fileLastModified;
    String simKey = "";
    long rootId;

    private static MyUtility instance = new MyUtility();

    public static MyUtility getInstance() {
        return instance;
    }

    /*
    Check for duplicates in stored urls.
     */
    private boolean checkPath(long k) {
        for (int i = 0; i < urlPaths.length; i++) {
            if (k == urlPaths[i]) {
                return false;
            }
        }
        return true;
    }

    /*
    Converts the url key string into the persisted frequency table file name.
     */
    public static Path getFilePath(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
            url = url.replace("//", " ").replace("/", " ").replace(":", "").replace("<", "").replace(">", "").replace("*", "").replace("?", "");
        } else {
            url = url.replace("//", " ").replace("/", " ").replace(":", "").replace("<", "").replace(">", "").replace("*", "").replace("?", "");
        }
        url = url + ".ser";
        Path p = Paths.get(BTree.Path, url);
        return p;
    }

    /*
    Retrieves the node count from the BTree file.
     */
    public int getNodeCount() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(BTree.BTreePath, "rw");
        raf.seek(4);
        FileChannel f = raf.getChannel();
        ByteBuffer b = ByteBuffer.allocate(4);
        f.read(b);
        b.position(0);
        int id = b.getInt();
        return id;
    }

    /*
    Compares the given website frequency table to each medoid and returns the most similar medoid.
     */
    public String getClusterCategory(FrequencyTable ft, Object medoidLock) throws IOException, ClassNotFoundException {
        Medoid medoids[] = readClusters(medoidLock);
        double sim = 0;
        double tmpSim;
        String medoidKey = "";

        for (int i = 0; i < medoids.length; i++) {
            synchronized (medoidLock) {
                tmpSim = cosineSimMetric(ft, getFrequencyTable(medoids[i].key));
            }
            if (tmpSim > sim) {
                sim = tmpSim;
                medoidKey = medoids[i].key;
            }
        }
        return medoidKey;
    }

    /*
    Calculates the cosine similarity between two frequency tables.
     */
    public double cosineSimMetric(FrequencyTable b, FrequencyTable d) {
        double sim;
        double magA = 0, magB = 0;
        double dotAB = 0;
        int metaWeight = 2;
        metaWeight = metaWeight * metaWeight;
        MyHashElement e;
        int tmp;

        if (b.frequencies != null) {
            for (int i = 0; i < b.frequencies.getTableSize(); i++) {
                e = b.frequencies.getElement(i);

                while (e != null) {
                    if (!(e instanceof Deleted)) {
                        if (e instanceof MetaHashNode) {
                            magB = magB + e.getVal() * e.getVal() * metaWeight;
                            tmp = d.frequencies.get(e.getKey(), "meta");
                            dotAB = dotAB + e.getVal() * tmp * metaWeight;
                        } else {
                            magB = magB + e.getVal() * e.getVal();
                            tmp = d.frequencies.get(e.getKey(), "");
                            dotAB = dotAB + e.getVal() * tmp;
                        }
                    }
                    e = e.getNext();
                }
            }

            for (int j = 0; j < d.frequencies.getTableSize(); j++) {
                e = d.frequencies.getElement(j);
                while (e != null) {
                    if (!(e instanceof Deleted)) {
                        if (e instanceof MetaHashNode) {
                            magA = magA + e.getVal() * e.getVal() * metaWeight;
                        } else {
                            magA = magA + e.getVal() * e.getVal();
                        }
                    }
                    e = e.getNext();
                }
            }
            magB = Math.sqrt(magB);
            magA = Math.sqrt(magA);
            sim = dotAB / (magA * magB);
            return sim;
        } else {
            return 0;
        }
    }

    /*
    K-medoids clustering algorithm that computes 10 medoids for the persisted data set of website keys and frequency
    tables. Uses cosine similarity as closeness metric between data points. Writes out the medoid website keys to a file
    for quick retrieval.
     */
    public void cacheClusters() throws IOException, ClassNotFoundException {
        MyURLs initUrls = MyURLs.getInstance();
        nodeCount = getNodeCount();
        String tmpUrl;
        FrequencyTable freq;
        double tmpSim;
        double sim = 0;
        int index = 0;
        String mostSimilar = "";
        byte toWrite[];

        Medoid medoids[] = new Medoid[10];
        for (int i = 0; i < 10; i++) {
            medoids[i] = new Medoid();
            medoids[i].key = initUrls.URLS[i].url;
            medoids[i].medoidFrequencies = getFrequencyTable(initUrls.URLS[i].url);
            System.out.println(cosineSimMetric(medoids[i].medoidFrequencies,medoids[i].medoidFrequencies));
        }

        for (int iteration = 0; iteration < 1000; iteration++) { //update medoids for 1000 times or break
            medoids = associate(medoids);
            for (int i = 0; i < medoids.length; i++) {
                medoids[i].cost = calcCost(medoids[i]);
            }
            Medoid updatedMedoids[] = update(medoids);

            if (updatedMedoids != null) {
                medoids = updatedMedoids;
            } else {
                break;
            }
        }

        RandomAccessFile raf = new RandomAccessFile(MedoidsPath, "rw");
        FileChannel f = raf.getChannel();
        ByteBuffer b = ByteBuffer.allocate(270 * numberOfMedoids); //max key length by 10 keys (10 medoids)

        for (int i = 0; i < medoids.length; i++) {
            b.putInt(medoids[i].key.length());
            toWrite = medoids[i].key.getBytes();
            b.put(toWrite);
        }
        b.flip();
        while(b.hasRemaining()) {
            f.write(b);
        }
        f.close();
        raf.close();
    }

    /*
    Associates each data point (website url key) to a medoid.
     */
    public Medoid[] associate(Medoid meds[]) throws IOException, ClassNotFoundException {
        FrequencyTable freq;
        double tmpSim;
        double sim = 0;
        int index = 0;
        String key;

        for (int i = 1; i <= nodeCount; i++) {
            BNode tmp = new BNode(BTree.childNum);
            tmp.readNode(i);
            for (int j = 0; j < BTree.childNum-2; j++) {
                key = tmp.keys[j];
                if (key != null && !isMedoid(key, meds)) {
                    sim = 0;
                    freq = getFrequencyTable(key);
                    for (int k = 0; k < meds.length; k++) {
                        tmpSim = cosineSimMetric(meds[k].medoidFrequencies,freq);
                        if (tmpSim > sim) {
                            sim = tmpSim;
                            index = k;
                        }
                    }
                    meds[index].associated.add(key);
                } else {
                    break;
                }
            }
        }
        return meds;
    }

    /*
    Calculates the total cost of the system using the sum of cosine similarity values between each data point and its
    medoid.
     */
    public double calcCost(Medoid medoid) throws IOException, ClassNotFoundException {
        double cost = 0;
        for (int j = 0; j < medoid.associated.size(); j++) {
            cost = cost + cosineSimMetric(medoid.medoidFrequencies, getFrequencyTable(medoid.associated.get(j)));
        }
        return cost;
    }

    /*
    Finds new medoids for each cluster and returns arraylist of new medoids.
     */
    public Medoid[] update(Medoid meds[]) throws IOException, ClassNotFoundException {
        int tlr;
        Medoid tmp; // = new Medoid();
        double cost;
        Boolean updatedAtLeastOne = false;

        for (int i = 0; i < meds.length; i++) {
            if (meds[i].associated.size() > 1) {
                tlr = ThreadLocalRandom.current().nextInt(0, meds[i].associated.size() - 1);
            } else {
                tlr = 0;
            }
            tmp = new Medoid();
            tmp.key = meds[i].associated.get(tlr);

            tmp.associated.add(meds[i].key);
            for (int j = 0; j < meds[i].associated.size(); j++) {
                if (j != tlr) {
                    tmp.associated.add(meds[i].associated.get(j));
                }
            }
            tmp.medoidFrequencies = getFrequencyTable(tmp.key);

            cost = calcCost(tmp);
            if (cost > meds[i].cost && cost != 0) {
                meds[i] = tmp;
                updatedAtLeastOne = true;
            }
            meds[i].associated = new ArrayList<>();
        }
        if (updatedAtLeastOne) {
            return meds;
        } else {
            return null;
        }
    }

    /*
    Determines if the given key is a medoid.
     */
    public Boolean isMedoid(String k, Medoid medoids[]) {
        for (int i = 0; i < medoids.length; i++) {
            if (medoids[i].key.equals(k)) {
                return true;
            }
        }
        return false;
    }

    /*
    Fetches persisted medoid keys from disk.
     */
    public Medoid[] readClusters(Object medoidLock) throws IOException {
        Medoid medoids[] = new Medoid[numberOfMedoids];

        synchronized (medoidLock) {
            RandomAccessFile raf = new RandomAccessFile(MedoidsPath, "rw");
            FileChannel f = raf.getChannel();
            ByteBuffer b = ByteBuffer.allocate(270 * numberOfMedoids); //max key length by 10 keys (10 medoids)
            f.read(b);
            b.position(0);

            for (int i = 0; i < numberOfMedoids; i++) {
                medoids[i] = new Medoid();
                int len = b.getInt();
                byte[] bytes = new byte[len];
                b.get(bytes);
                medoids[i].key = new String(bytes);
            }
            f.close();
            raf.close();
        }
        return medoids;
    }

    /*
    Retrieves persisted frequency table for the given key.
     */
    public FrequencyTable getFrequencyTable(String URL) throws IOException, ClassNotFoundException {
        Path a = MyUtility.getFilePath(URL);
        FileInputStream fis = new FileInputStream(a.toFile());
        ObjectInputStream is = new ObjectInputStream(fis);
        FrequencyTable freq = new FrequencyTable();
        freq.frequencies.readObject(is);
        is.close();
        fis.close();
        return freq;
    }

    /*
    Fetch is used to retrieve keys from the B-Tree.  The method does a depth-wise traversal of the tree and returns a
    key based upon the input parameters.

    String url = the input web url key.

    Object lock = a lock to avoid concurrency issues between fetch and the file cache updater thread.

    String fetch type:
    1) fetchType = most similar: returns the web url key whose frequency table is most similar to the input website.
    2) fetchType = oldest file: returns the file key of the oldest frequency table file.
    */
    public String fetch(String url, Object lock, String fetchType) throws IOException, ClassNotFoundException {
        sim = 0;
        lastModified = Long.MAX_VALUE;

        rootId = BTree.readRootId();
        BNode e = new BNode(BTree.childNum);
        e.readNode(rootId);

        if (fetchType.equals("most similar")) {
            HTMLParser urlParser = new HTMLParser();
            FrequencyTable urlFreq = urlParser.parseURL(url);

            //Entry point for the full B-Tree traversal.
            leftMost(e,urlFreq,lock,fetchType);
            return simKey;
        } else {
            //Entry point for the full B-Tree traversal.
            leftMost(e,null,lock,fetchType);

            return fileKeyLastModified;
        }
    }

    /*
    Calculates cosine similarity for all node keys and the given url key and keeps the most similar.
    */
    public void compareNodeKeys(BNode e, FrequencyTable freq, Object lock, String fetchType) throws IOException, ClassNotFoundException {
        if (fetchType.equals("most similar")) {
            double simTmp;
            FrequencyTable tmpFreq;
            for (int i = 0; i < e.keys.length; i++) {
                if (e.keys[i] != null) {
                    synchronized (lock) {
                        tmpFreq = getFrequencyTable(e.keys[i]);
                    }
                    simTmp = cosineSimMetric(tmpFreq, freq);
                    if (simTmp > sim) {
                        sim = simTmp;
                        simKey = e.keys[i];
                    }
                }
            }
        } else if (fetchType.equals("oldest file")) {
            for (int i = 0; i < e.keys.length; i++) {
                if (e.keys[i] != null) {
                    Path a = getFilePath(e.keys[i]);
                    File file = a.toFile();
                    if (file.lastModified() < lastModified) {
                        lastModified = file.lastModified();
                        fileKeyLastModified = e.keys[i];
                        fileLastModified = file;
                    }
                }
            }
        }
    }

    /*
    Goes to the left most node with respect to the input node.
    */
    public void leftMost(BNode e, FrequencyTable urlFreq, Object lock, String fetchType) throws IOException, ClassNotFoundException {
        long tmpChild = 0;
        while (e.children[0] != 0) {
            e.usedForBreadthSearching++;
            e.writeNode();
            tmpChild = e.children[0];
            e = new BNode(BTree.childNum);
            e.readNode(tmpChild);
        }
        compareNodeKeys(e, urlFreq, lock, fetchType);
        moveToParent(e, urlFreq, lock, fetchType);
    }

    /*
    Move up to parent node. Calls leftMost if not all children nodes have been traversed yet.
    Otherwise, does cosine comparison of key frequency tables to given url and calls itself if not already at root node.
    */
    public void moveToParent(BNode e, FrequencyTable urlFreq, Object lock, String fetchType) throws IOException, ClassNotFoundException {
        long tmpParent = e.parent;
        long tmpChild;
        e = new BNode(BTree.childNum);
        e.readNode(tmpParent);

        if ((e.usedForBreadthSearching != e.children.length) && (e.children[e.usedForBreadthSearching] != 0)) {
            tmpChild = e.children[e.usedForBreadthSearching];
            e.usedForBreadthSearching++;
            e.writeNode();
            e = new BNode(BTree.childNum);
            e.readNode(tmpChild);
            leftMost(e, urlFreq, lock, fetchType);
        } else {
            compareNodeKeys(e, urlFreq, lock, fetchType);
            e.usedForBreadthSearching = 0;
            e.writeNode();
            if (e.id != rootId) {
                moveToParent(e, urlFreq, lock, fetchType);
            }
        }
    }
}
