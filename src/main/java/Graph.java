import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class Graph {
    HashMap<String, ArrayList<MyConnection>> nodes;
    SpanningTree shortestPathTree;
    BNode root;
    static long currentmark;

    public Graph() throws IOException {
        nodes = new HashMap<>();
        root = new BNode(8);
        long rootID = BTree.readRootId();
        root.readNode(rootID);
    }

    //======================================================
    public void findConnections(BNode r, int count) throws IOException, ClassNotFoundException {
        BNode e;
        Document doc;
        Elements links;
        MyUtility utl = new MyUtility();

        for (int i = 0; i < r.children.length; i++) {
            if (r.children[i] != 0) {
                e = new BNode(8);
                e.readNode(r.children[i]);
                findConnections(e, count);
            }
        }

        for (int i = 0; i < r.keys.length; i++) {
            if (r.keys[i] != null) {
                ArrayList<MyConnection> cons = new ArrayList<>();
                doc = Jsoup.connect(r.keys[i]).get();
                links = doc.select("a[href]");
                for (Element element : links) {
                    boolean contained = false;
                    if (BTree.searchReturnBool(root, element.attr("abs:href")) && !element.attr("abs:href").equals(r.keys[i])) {
                        MyConnection mc = new MyConnection();

                        FrequencyTable c1 = utl.getFrequencyTable(element.attr("abs:href"));
                        FrequencyTable c2 = utl.getFrequencyTable(r.keys[i]);

                        mc.weight = utl.cosineSimMetric(c1, c2);
                        mc.url = element.attr("abs:href");
                        mc.parent = r.keys[i];
                        for (MyConnection con : cons) {
                            if (con.url.split(":")[1].equals(mc.url.split(":")[1]) || mc.url.split(":")[1].equals(r.keys[i].split(":")[1])) {
                                contained = true;
                            }
                        }
                        if (!contained) {
                            cons.add(mc);
                        }
                    }
                }
                System.out.println("========================================");
                System.out.println(r.keys[i]);
                System.out.println(links.size());
                System.out.println(cons.size());
                for (MyConnection con : cons) {
                    System.out.println(con.url);
                }
                nodes.put(r.keys[i],cons);
            }
        }
    }

    //===================================================
    public SpanningTree findMinimumSpanningTree(WebsiteNode node) {
        SpanningTree st = new SpanningTree(node);
        st = prim(st);
        return st;
    }

    //===================================================
    public Path findNearestClusterCenter(String src) throws IOException {
        ArrayList<String> clusterCenters = new ArrayList<>();
        ArrayList<Path> paths = new ArrayList<>();
        MyUtility utl = new MyUtility();
        Medoid medoids[] = utl.readClusters(new Object());
        Path shortestPath = new Path();

        WebsiteNode userSelected = new WebsiteNode();
        userSelected.url = src;
        userSelected.connections = nodes.get(src);
        shortestPathTree = new SpanningTree(userSelected);

        //Use dijkstra's algorithm to find the shortest-path spanning tree with the selected
        //website as the root node
        shortestPathTree = dijkstra(shortestPathTree);

        for (Medoid m : medoids) {
//            System.out.println(m.key);
            if (shortestPathTree.contains(m.key)) {
                clusterCenters.add(m.key);
            }
        }

        if (clusterCenters.size() != 0) {
            double totalDistance = Double.MAX_VALUE;
            WebsiteNode clusterCenter;
            WebsiteNode nearestClusterCenter = new WebsiteNode();

            System.out.println("Connected clusters: ");

            for (String key : clusterCenters) {
                System.out.println(key);
                clusterCenter = shortestPathTree.get(key);
                if (clusterCenter.distance < totalDistance) {
                    nearestClusterCenter = clusterCenter;
                    totalDistance = clusterCenter.distance;
                }
            }
            System.out.println("The closest cluster is: ");
            System.out.println(nearestClusterCenter.url);
            shortestPath.src = userSelected;
            shortestPath.dst = nearestClusterCenter;
            shortestPath.pathLength = totalDistance;
            return shortestPath;
        } else {
            System.out.println("No cluster center in spanning tree.");
            return null;
        }
    }

    //===================================================
    private SpanningTree dijkstra(SpanningTree spanningTree) {
        ArrayList<WebsiteNode> arr = new ArrayList<>();
        PriorityQueue<MyConnection> pq = new PriorityQueue(new CompareByDistance());
        MyConnection mc;
        WebsiteNode webNode;
        ArrayList<String> visited = new ArrayList<>();
        ArrayList<MyConnection> toRemove = new ArrayList<>();
        double newDist;

        webNode = spanningTree.root;
        webNode.distance = 0;
        for (int i = 0; i < webNode.connections.size(); i++) {
            webNode.connections.get(i).distance = webNode.distance + (1 - webNode.connections.get(i).weight);
        }
        pq.addAll(spanningTree.root.connections);
        spanningTree.root.visited = true;
        arr.add(spanningTree.root);
        visited.add(spanningTree.root.url);

        while (!pq.isEmpty()) {
            mc = pq.remove();
            webNode = new WebsiteNode();
            webNode.url = mc.url;
            webNode.connections = nodes.get(mc.url);
            webNode.distance = mc.distance;
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
                    newDist = webNode.distance + (1 - con.weight);
                    if (newDist < con.distance) {
                        con.distance = newDist;
                    }
                    pq.add(con);
                }
            }
        }
        return spanningTree;
    }

    //===================================================
    public SpanningTree prim(SpanningTree spanningTree) {
        ArrayList<WebsiteNode> arr = new ArrayList<>();
        PriorityQueue<MyConnection> pq = new PriorityQueue(new CompareByDistance());
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
    public void writeNodes() throws IOException {
        FileOutputStream fos = new FileOutputStream("graphNodes.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(nodes);
        oos.close();
        fos.close();
    }
    public void readNodes() throws IOException, ClassNotFoundException {
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

class CompareByDistance implements Comparator<MyConnection> {

    public int compare(MyConnection a, MyConnection b) {
        int out;
        double cmp = a.distance - b.distance;
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