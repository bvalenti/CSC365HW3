import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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
//            BTree.validateAndUpdateUrls(root);
            graph.findConnections(root, 0);
            graph.writeNodes();
        }

        if (true) {
            String url = "https://en.wikipedia.org/wiki/History_of_the_United_States";
            graph.readNodes();

//            Iterator it = graph.nodes.values().iterator();
//            while (it.hasNext()) {
//                System.out.println("=================================================");
//                ArrayList<MyConnection> arr = (ArrayList<MyConnection>) it.next();
//                for (int i = 0; i < arr.size(); i++) {
//                    System.out.println(arr.get(i).url);
//                    System.out.println(arr.get(i).parent);
//                }
//            }

            graph.findNearestClusterCenter(url);
            TreePlotDisplay display = new TreePlotDisplay(graph);

//            SpanningTreePrinter myPrinter = new SpanningTreePrinter(graph);
//            myPrinter.setSize(Toolkit.getDefaultToolkit().getScreenSize().width,
//                    Toolkit.getDefaultToolkit().getScreenSize().height);
//            myPrinter.setVisible(true);
//            myPrinter.printerPaint();
        }
    }
}