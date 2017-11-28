import com.sun.jmx.remote.internal.ArrayQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class Graph {
    HashMap<String, ArrayList<MyConnection>> nodes;
    BNode root;
    static long currentmark;

    public Graph() throws IOException {
        nodes = new HashMap<>();
        root = new BNode(8);
        long rootID = BTree.readRootId();
        root.readNode(rootID);
    }

    //======================================================
    public void findConnections(BNode r) throws IOException, ClassNotFoundException {
        BNode e;
        Document doc;
        Elements links;
        MyUtility utl = new MyUtility();

        if (r.isLeaf == 1) {
            for (int i = 0; i < r.keys.length; i++) {
                ArrayList<MyConnection> cons = new ArrayList<>();
                doc = Jsoup.connect(r.keys[i]).get();
                links = doc.select("a[href]");
                for (Element element : links) {
                    if (BTree.searchReturnBool(root,element.attr("abs:href"))) {
                        MyConnection mc = new MyConnection();

                        FrequencyTable c1 = utl.getFrequencyTable(element.attr("abs:href"));
                        FrequencyTable c2 = utl.getFrequencyTable(r.keys[i]);

                        mc.weight = utl.cosineSimMetric(c1,c2);
                        mc.url = element.attr("abs:href");
                        mc.parent = r.keys[i];
                        cons.add(mc);
                    }
                }
                nodes.put(r.keys[i],cons);
            }
            return;
        }

        for (int i = 0; i < r.children.length; i++) {
            e = new BNode(8);
            e.readNode(r.children[i]);
            findConnections(e);
        }

        for (int i = 0; i < r.keys.length; i++) {
            ArrayList<MyConnection> cons = new ArrayList<>();
            doc = Jsoup.connect(r.keys[i]).get();
            links = doc.select("a[href]");
            for (Element element : links) {
                if (BTree.searchReturnBool(root,element.attr("abs:href"))) {
                    MyConnection mc = new MyConnection();

                    FrequencyTable c1 = utl.getFrequencyTable(element.attr("abs:href"));
                    FrequencyTable c2 = utl.getFrequencyTable(r.keys[i]);

                    mc.weight = utl.cosineSimMetric(c1,c2);
                    mc.url = element.attr("abs:href");
                    mc.parent = r.keys[i];
                    cons.add(mc);
                }
            }
            nodes.put(r.keys[i],cons);
        }
    }

    //===================================================
    public SpanningTree findMinimumSpanningTree(WebsiteNode node) {
        SpanningTree st = new SpanningTree(node);
        st = prim(st);
        return st;
    }

    //===================================================
    public SpanningTree prim(SpanningTree spanningTree) {
        ArrayList<WebsiteNode> arr = new ArrayList<>();
        PriorityQueue<MyConnection> pq = new PriorityQueue(new CompareByWeight());
        MyConnection mc;
        WebsiteNode webNode;
        ArrayList<String> visited = new ArrayList<>();
        ArrayList<MyConnection> toRemove = new ArrayList<>();

        pq.addAll(spanningTree.root.connections);
        spanningTree.root.visited = true;
        arr.add(spanningTree.root);
        visited.add(spanningTree.root.url);

        while (!pq.isEmpty()) {
            mc = pq.remove();
            webNode = new WebsiteNode();
            webNode.url = mc.url;
            webNode.connections = nodes.get(mc.url);
            webNode.visited = true;
            for (int i = 0; i < arr.size(); i++) {
                if (mc.parent.equals(arr.get(i).url)) {
                    arr.get(i).spanningTreeConnections.add(webNode);
                    break;
                }
            }
            arr.add(webNode);
            visited.add(webNode.url);

            //Remove MyConnections pointing to WebNodes that have already been visited.
            Iterator it = pq.iterator();
            while(it.hasNext()) {
                MyConnection m = (MyConnection) it.next();
                if (visited.contains(m.url)) {
                    toRemove.add(m);
                }
            }
            pq.removeAll(toRemove);

            //Add new MyConnections to the priority queue that point to WebNodes that haven't been visited yet.
            for (MyConnection con : webNode.connections) {
                if (!visited.contains(con.url)) {
                    pq.add(con);
                }
            }
        }
        return spanningTree;
    }

//    //======================================================
//    public boolean search(WebsiteNode src, WebsiteNode dst) {
//        long m = ++currentmark;
//        ArrayDeque<WebsiteNode> stack = new ArrayDeque<>();
//        stack.addFirst(src);
//        while (!stack.isEmpty()) {
//            WebsiteNode e = stack.removeLast();
//            if (e.marker != m) {
//                e.marker = m;
//            }
//            if (e == dst) {
//                return true;
//            }
//            for (MyConnection s : e.connections) {
//                WebsiteNode p = new WebsiteNode();
//                p.url = s.url;
//                p.connections = nodes.get(s);
//                stack.addFirst(p);
//            }
//        }
//        return false;
//    }

    //======================================================
    public void writeConnections() throws IOException {
        FileOutputStream fos = new FileOutputStream("graphNodes.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(nodes);
        oos.close();
        fos.close();
    }
    public void readConnections() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream("graphNodes.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        nodes = (HashMap) ois.readObject();
        ois.close();
        fis.close();
    }
}

class CompareByWeight implements Comparator<MyConnection> {

    public int compare(MyConnection a, MyConnection b) {
        int out;
        double cmp = a.weight - b.weight;
        if (cmp > 0) {
            out = 1;
        } else if (cmp < 0) {
            out = -1;
        } else {
            out = 0;
        }
        return out;
    }
}