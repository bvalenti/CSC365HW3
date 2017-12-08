import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        Graph graph = new Graph();

        long rootId = BTree.readRootId();
        BNode root = new BNode(BTree.childNum);
        root.readNode(rootId);

        BTree.printTree(root);

        //Checks each url in the B-Tree to determine if it still links to a valid website.
        if (false) {
            BTree.validateAndUpdateUrls(root);
        }

        //Persist the Hashtable containing the graph
        if (false) {
            graph.findConnections(root, 0);
            graph.writeNodes();
        }
        if (true) {
            graph.readNodes();
            graph.addParentConnections();
            graph.writeNodes();
        }
    }
}