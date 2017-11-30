import java.io.IOException;

public class Main {
    static int count;

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        Graph graph = new Graph();

        long rootId = BTree.readRootId();
        BNode root = new BNode(BTree.childNum);
        root.readNode(rootId);

//        BTree.printTree(root);
        //Persist the Hashtable containing the graph
        if (false) {
            graph.findConnections(root, 0);
            graph.writeNodes();
        }

        if (true) {
            String url = "https://en.wikipedia.org/wiki/Japanese_language";
            graph.readNodes();
            graph.findNearestClusterCenter(url);
        }
    }
}
