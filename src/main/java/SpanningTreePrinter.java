import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class SpanningTreePrinter extends JPanel {
    SpanningTree tree;
    Graph currentGraph;
    Path nearestCluster;
    MyUtility utl = new MyUtility();
    ArrayList<PlottingNode> toPlot = new ArrayList<>();
    ArrayList<PlottingNode> plottable = new ArrayList<>();
    ArrayList<String> strings;
    Medoid medoids[] = utl.readClusters(new Object());

    SpanningTreePrinter(Graph graph, ArrayList<String> strings) throws IOException {
        currentGraph = graph;
        tree = graph.shortestPathTree;
        this.strings = strings;
    }

    SpanningTreePrinter(Graph graph, ArrayList<String> strings, Path path) throws IOException {
        currentGraph = graph;
        tree = graph.shortestPathTree;
        nearestCluster = path;
        this.strings = strings;
    }

    @Override
    public void paintComponent(Graphics g) {
        int layer = -1, maxLayer, count;
        int center = this.getWidth()/2;
        String urlNum;
        ArrayList<PlottingNode> graphRow;
        WebsiteNode root = tree.root;
        PlottingNode pnode;
        recursePaint(root,layer);

        if (true) {
            plotClusters(g);
        }

        if (false) { //Plots only the paths to the cluster centers.
            maxLayer = getMaxLayer(toPlot);
            for (int i = 0; i <= maxLayer; i++) {
                graphRow = new ArrayList<>();
                for (PlottingNode n : toPlot) {
                    if (n.layer == i) {
                        graphRow.add(n);
                    }
                }

                int horizontalSpacing = getWidth() / graphRow.size();
                int verticalSpacing = getHeight() / (maxLayer + 1);
                if (graphRow.size() % 2 == 0) {
                    count = 0;
                    for (int j = (-graphRow.size() / 2); j < graphRow.size() / 2; j++) {
                        PlottingNode node = new PlottingNode(graphRow.get(count).websiteNode, i);
                        node.x = center + j * horizontalSpacing + horizontalSpacing / 2 - 10;
                        node.y = 10 + i * verticalSpacing;
                        plottable.add(node);
                        count++;
                    }
                } else {
                    count = 0;
                    for (int j = (-graphRow.size() / 2); j <= graphRow.size() / 2; j++) {
                        PlottingNode node = new PlottingNode(graphRow.get(count).websiteNode, i);
                        node.x = center + j * horizontalSpacing - 10;
                        node.y = 10 + i * verticalSpacing;
                        plottable.add(node);
                        count++;
                    }
                }
            }


            pnode = plottable.get(0);
            urlNum = Integer.toString(getUrlNum(plottable.get(0).websiteNode.url));
            g.drawRect(pnode.x, pnode.y, 10, 10);
            g.drawString(urlNum, pnode.x, pnode.y);

            for (Medoid m : medoids) {
                if (tree.contains(m.key)) {
                    for (PlottingNode p : plottable) {
                        if (p.websiteNode.url.equals(m.key)) {
                            while (p.websiteNode.parentString != null) {
                                int x = p.x, y = p.y, i;
                                urlNum = Integer.toString(getUrlNum(p.websiteNode.url));
                                g.drawRect(p.x, p.y, 10, 10);
                                g.drawString(urlNum, p.x, p.y);
                                for (i = 0; i < plottable.size(); i++) {
                                    if (p.websiteNode.parentString.equals(plottable.get(i).websiteNode.url)) {
                                        break;
                                    }
                                }
                                g.drawLine(x, y, plottable.get(i).x, plottable.get(i).y);
                                p = plottable.get(i);
                            }
                            g.drawRect(p.x, p.y, 10, 10);
                        }
                    }
                }
            }

            if (nearestCluster != null) {
                g.setColor(Color.GREEN);
                PlottingNode p = null;
                for (int i = 0; i < plottable.size(); i++) {
                    p = plottable.get(i);
                    if (p.websiteNode.url.equals(nearestCluster.dst.url)) {
                        break;
                    }
                }
                if (p != null) {
                    while (p.websiteNode.parentString != null) {
                        int x = p.x, y = p.y, i;
                        urlNum = Integer.toString(getUrlNum(p.websiteNode.url));
                        g.drawRect(p.x, p.y, 10, 10);
                        g.drawString(urlNum, p.x, p.y);
                        for (i = 0; i < plottable.size(); i++) {
                            if (p.websiteNode.parentString.equals(plottable.get(i).websiteNode.url)) {
                                break;
                            }
                        }
                        g.drawLine(x, y, plottable.get(i).x, plottable.get(i).y);
                        p = plottable.get(i);
                    }
                    g.drawRect(p.x, p.y, 10, 10);
                }
            }
        }

        if (false) { //Plots entire spanning tree.
            maxLayer = getMaxLayer(toPlot);
            for (int i = 0; i <= maxLayer; i++) {
                graphRow = new ArrayList<>();
                for (PlottingNode n : toPlot) {
                    if (n.layer == i) {
                        graphRow.add(n);
                    }
                }

                int horizontalSpacing = getWidth() / graphRow.size();
                int verticalSpacing = getHeight() / (maxLayer + 1);
                if (graphRow.size() % 2 == 0) {
                    count = 0;
                    for (int j = (-graphRow.size() / 2); j < graphRow.size() / 2; j++) {
                        PlottingNode node = new PlottingNode(graphRow.get(count).websiteNode, i);
                        node.x = center + j * horizontalSpacing + horizontalSpacing / 2 - 10;
                        node.y = 10 + i * verticalSpacing;
                        plottable.add(node);
                        count++;
                    }
                } else {
                    count = 0;
                    for (int j = (-graphRow.size() / 2); j <= graphRow.size() / 2; j++) {
                        PlottingNode node = new PlottingNode(graphRow.get(count).websiteNode, i);
                        node.x = center + j * horizontalSpacing - 10;
                        node.y = 10 + i * verticalSpacing;
                        plottable.add(node);
                        count++;
                    }
                }
            }


            pnode = plottable.get(0);
            urlNum = Integer.toString(getUrlNum(plottable.get(0).websiteNode.url));
            g.drawRect(pnode.x, pnode.y, 10, 10);
            g.drawString(urlNum, pnode.x, pnode.y);

            for (int i = 1; i < plottable.size(); i++) {
                pnode = plottable.get(i);
                urlNum = Integer.toString(getUrlNum(plottable.get(i).websiteNode.url));
                g.drawRect(pnode.x, pnode.y, 10, 10);
                g.drawString(urlNum, pnode.x, pnode.y);
                for (PlottingNode p : plottable) {
                    if (p.websiteNode.url.equals(pnode.websiteNode.parentString)) {
                        g.drawLine(pnode.x, pnode.y, p.x, p.y);
                        break;
                    }
                }
            }

            if (nearestCluster != null) {
                g.setColor(Color.GREEN);
                PlottingNode p = null;
                for (int i = 0; i < plottable.size(); i++) {
                    p = plottable.get(i);
                    if (p.websiteNode.url.equals(nearestCluster.dst.url)) {
                        break;
                    }
                }
                if (p != null) {
                    while (p.websiteNode.parentString != null) {
                        int x = p.x, y = p.y, i;
                        urlNum = Integer.toString(getUrlNum(p.websiteNode.url));
                        g.drawRect(p.x, p.y, 10, 10);
                        g.drawString(urlNum, p.x, p.y);
                        for (i = 0; i < plottable.size(); i++) {
                            if (p.websiteNode.parentString.equals(plottable.get(i).websiteNode.url)) {
                                break;
                            }
                        }
                        g.drawLine(x, y, plottable.get(i).x, plottable.get(i).y);
                        p = plottable.get(i);
                    }
                    g.drawRect(p.x, p.y, 10, 10);
                }
            }
        }
    }

    //=============================================
    public void plotClusters(Graphics g) {
        ArrayList<PlottingNode> plottingNodes = new ArrayList<>();
        PlottingNode n = null;
        ArrayList<PlottingNode> graphRow;
        int i, maxLayer, numMedoids = 0, count;
        int center = this.getWidth()/2;

        for (Medoid m : medoids) {
            if (tree.contains(m.key)) {
                numMedoids++;
                for (PlottingNode p : toPlot) {
                    if (p.websiteNode.url.equals(m.key)) {
                        plottingNodes.add(p);
                    }
                }
            }
        }
        for (int j = 0; j < numMedoids; j++) {
            PlottingNode p = plottingNodes.get(j);
            while(p.websiteNode.parentString != null) {
                for (i = 0; i < toPlot.size(); i++) {
                    if (toPlot.get(i).websiteNode.url.equals(p.websiteNode.parentString)) {
                        break;
                    }
                }
                if (!contains(plottingNodes,toPlot.get(i))) {
                    plottingNodes.add(toPlot.get(i));
                }
                p = toPlot.get(i);
            }
        }

        maxLayer = getMaxLayer(plottingNodes);
        int horizontalSpacing = getWidth()/numMedoids;
        int verticalSpacing = getHeight()/(maxLayer + 1);

        for (i = 0; i <= maxLayer; i++) {
            graphRow = new ArrayList<>();
            for (PlottingNode pn : plottingNodes) {
                if (pn.layer == i) {
                    graphRow.add(pn);
                }
            }
            if (i == 0) {
                PlottingNode node = new PlottingNode(graphRow.get(0).websiteNode, i);
                node.x = center - 10;
                node.y = 10 + i * verticalSpacing;
                plottable.add(node);
            } else {
                if (numMedoids % 2 == 0) {
                    count = 0;
                    for (int j = (-numMedoids/2); j < numMedoids/2; j++) {
                        PlottingNode node = new PlottingNode(graphRow.get(count).websiteNode, i);
                        node.x = center + j * horizontalSpacing + horizontalSpacing/2 - 10;
                        node.y = 10 + i * verticalSpacing;
                        plottable.add(node);
                        count++;
                        if (count == graphRow.size()) {
                            break;
                        }
                    }
                } else {
                    count = 0;
                    for (int j = (-numMedoids/2); j <= numMedoids/2; j++) {
                        PlottingNode node = new PlottingNode(graphRow.get(count).websiteNode, i);
                        node.x = center + j * horizontalSpacing - 10;
                        node.y = 10 + i * verticalSpacing;
                        plottable.add(node);
                        count++;
                        if (count == graphRow.size()) {
                            break;
                        }
                    }
                }
            }
        }
        plotNodes(g);
    }

    //========================================
    public void plotNodes(Graphics g) {
        PlottingNode pnode;
        String urlNum;

        pnode = plottable.get(0);
        urlNum = Integer.toString(getUrlNum(plottable.get(0).websiteNode.url));
        g.drawRect(pnode.x, pnode.y, 10, 10);
        g.drawString(urlNum, pnode.x, pnode.y);

        for (int i = 1; i < plottable.size(); i++) {
            pnode = plottable.get(i);
            urlNum = Integer.toString(getUrlNum(plottable.get(i).websiteNode.url));
            if (isMedoid(pnode)) {
                g.setColor(Color.RED);
            }
            g.drawRect(pnode.x, pnode.y, 10, 10);
            g.setColor(Color.BLACK);
            g.drawString(urlNum, pnode.x, pnode.y);
            for (PlottingNode p : plottable) {
                if (p.websiteNode.url.equals(pnode.websiteNode.parentString)) {
                    g.drawLine(pnode.x, pnode.y, p.x, p.y);
                    break;
                }
            }
        }

        if (true) {
            if (nearestCluster != null) {
                System.out.println(nearestCluster.dst.url);
                g.setColor(Color.GREEN);
                PlottingNode p = null;
                for (int i = 0; i < plottable.size(); i++) {
                    p = plottable.get(i);
                    if (p.websiteNode.url.equals(nearestCluster.dst.url)) {
                        break;
                    }
                }
                if (p != null) {
                    while (p.websiteNode.parentString != null) {
                        int x = p.x, y = p.y, i;
                        urlNum = Integer.toString(getUrlNum(p.websiteNode.url));
                        g.drawRect(p.x, p.y, 10, 10);
                        g.drawString(urlNum, p.x, p.y);
                        for (i = 0; i < plottable.size(); i++) {
                            if (p.websiteNode.parentString.equals(plottable.get(i).websiteNode.url)) {
                                break;
                            }
                        }
                        g.drawLine(x, y, plottable.get(i).x, plottable.get(i).y);
                        p = plottable.get(i);
                    }
                    g.drawRect(p.x, p.y, 10, 10);
                }
            }
        }
    }


    //=============================================
    public void recursePaint(WebsiteNode node, int layer) {
        layer++;
        for (int i = 0; i < node.spanningTreeConnections.size(); i++) {
            WebsiteNode e = node.spanningTreeConnections.get(i);
            recursePaint(e,layer);
        }
        PlottingNode pnode = new PlottingNode(node,layer);
        toPlot.add(pnode);
    }

    public void printerPaint() {
        printerRepaint();
    }

    public void printerRepaint() {
        validate();
        repaint();
    }

    public int getMaxLayer(ArrayList<PlottingNode> arr) {
        int out = 0;
        for (PlottingNode node : arr) {
            if (node.layer > out) {
                out = node.layer;
            }
        }
        return out;
    }

    public boolean contains(ArrayList<PlottingNode> arr, PlottingNode p) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).websiteNode.url.equals(p.websiteNode.url)) {
                return true;
            }
        }
        return false;
    }

    public boolean isMedoid(PlottingNode p) {
        for (Medoid m : medoids) { if (m.key.equals(p.websiteNode.url)) { return true; } }
        return false;
    }

    public int getUrlNum(String url) {
        int out = 0;
        for (String s : strings) {
            if (s.equals(url)) {
                break;
            } else {
                out++;
            }
        }
        return out;
    }
}
