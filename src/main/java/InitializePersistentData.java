import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InitializePersistentData {

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        MyUtility utl = MyUtility.getInstance();
//        MyURLs urls = MyURLs.getInstance();
        MyURLs urls = new MyURLs(true);
        HTMLParser parser = new HTMLParser();
        FrequencyTable tmp;
        ObjectOutputStream os;
        FileOutputStream f;
        String url;
        String urlss[] = new String[1000];

        //Scrapes for new websites.
        if (false) {
            urls.scrape();
        }

        //Processes each website and creates hashtables from scrapedURLS
        if (false) {
            byte[] toWrite;
            RandomAccessFile rf = new RandomAccessFile(urls.urlPath, "rw");
            FileChannel f1 = rf.getChannel();
            ByteBuffer b1 = ByteBuffer.allocate(270 * urls.scrapedURLS.size());
            for (int i = 0; i < urls.scrapedURLS.size(); i++) {
                b1.putInt(urls.scrapedURLS.get(i).url.length());
                toWrite = urls.scrapedURLS.get(i).url.getBytes();
                b1.put(toWrite);
            }
            b1.flip();
            while (b1.hasRemaining()) {
                f1.write(b1);
            }
            f1.close();
            rf.close();

            //Create the B-Tree
//            BTree btree = BTree.getInstance();
            BTree btree = new BTree(8,true);
            for (int i = 0; i < urls.scrapedURLS.size(); i++) {
                btree.insert(urls.scrapedURLS.get(i).url);
            }

            //Create and persist the frequency tables
            for (int i = 0; i < urls.scrapedURLS.size(); i++) {
                if (urls.scrapedURLS.get(i).url.endsWith("/")) {
                    url = urls.scrapedURLS.get(i).url.substring(0,urls.scrapedURLS.get(i).url.length()-1);
                    url = url.replace("//"," ").replace("/"," ").replace(":","").replace("<","").replace(">","").replace("*","").replace("?","").replace("|","");
                } else {
                    url = urls.scrapedURLS.get(i).url.replace("//"," ").replace("/"," ").replace(":","").replace("<","").replace(">","").replace("*","").replace("?","").replace("|","");
                }
                url = url + ".ser";
                Path p = Paths.get(urls.path, url);
                File file = p.toFile();

                System.out.println(url);
                f = new FileOutputStream(file);
                os = new ObjectOutputStream(f);
                tmp = parser.parseURL(urls.scrapedURLS.get(i).url);
                tmp.frequencies.writeObject(os);
                os.close();
                f.close();
            }
        }

        //Processes each website and creates hashtables from the persisted url list
        if (false) {
            RandomAccessFile rf1 = new RandomAccessFile(urls.urlPath, "rw");
            FileChannel f2 = rf1.getChannel();
            ByteBuffer b2 = ByteBuffer.allocate(270 * 1000);
            f2.read(b2);
            b2.position(0);
            for (int i = 0; i < 1000; i++) {
                System.out.println(i);
                int len = b2.getInt();
                System.out.println(len);
                byte[] bytes = new byte[len];
                b2.get(bytes);
                urlss[i] = new String(bytes);
                System.out.println(urlss[i]);
            }
            f2.close();
            rf1.close();

//            BTree btree = BTree.getInstance();
            BTree btree = new BTree(8, true);
            for (int i = 0; i < 1000; i++) {
                btree.insert(urlss[i]);
            }

            //Create and persist the frequency tables
            for (int i = 0; i < urlss.length; i++) {
                if (urlss[i].endsWith("/")) {
                    url = urlss[i].substring(0,urlss[i].length()-1);
                    url = url.replace("//"," ").replace("/"," ").replace(":","").replace("<","").replace(">","").replace("*","").replace("?","").replace("|","");
                } else {
                    url = urlss[i].replace("//"," ").replace("/"," ").replace(":","").replace("<","").replace(">","").replace("*","").replace("?","").replace("|","");
                }
                url = url + ".ser";
                Path p = Paths.get(urls.path, url);
                File file = p.toFile();

                System.out.println(url);
                f = new FileOutputStream(file);
                os = new ObjectOutputStream(f);
                tmp = parser.parseURL(urlss[i]);
                tmp.frequencies.writeObject(os);
                os.close();
                f.close();
            }
        }

        // Find the cluster medoids and cache the keys in a file.
        utl.cacheClusters();
    }
}