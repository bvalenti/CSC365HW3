import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SpanningTreePrinter extends JPanel {
    SpanningTree tree;
    Graph currentGraph;
    ArrayList<PlottingNode> toPlot = new ArrayList<>();

    SpanningTreePrinter(Graph graph) {
        currentGraph = graph;
        tree = graph.shortestPathTree;
    }

    @Override
    public void paintComponent(Graphics g) {
        int layer = -1, maxLayer, count = 0;
        int center = this.getWidth()/2;
        ArrayList<PlottingNode> graphRow;
        WebsiteNode root = tree.root;
        recursePaint(root,layer);

        maxLayer = getMaxLayer(toPlot);
        for (int i = 0; i <= maxLayer; i++) {
            graphRow = new ArrayList<>();
            for (PlottingNode n : toPlot) {
                if (n.layer == i) {
                    graphRow.add(n);
                }
            }
            int spacing = getWidth()/graphRow.size();
            if (graphRow.size() % 2 == 0) {
                for (int j = (0-graphRow.size()/2); j < graphRow.size()/2; j++) {
//                    g.fillRect(center + j*spacing + spacing/2,100 + i*100,25,25);
                    g.drawRect(center + j*spacing + spacing/2,100 + i*100,25,25);
                }
            } else {
                for (int j = (0-graphRow.size()/2); j <= graphRow.size()/2; j++) {
                    g.drawRect(center + j*spacing,100 + i*100,25,25);
                }
            }
        }
    }

    public void recursePaint(WebsiteNode node, int layer) {
        layer++;
        for (int i = 0; i < node.spanningTreeConnections.size(); i++) {
            WebsiteNode e = new WebsiteNode();
            e.connections = currentGraph.nodes.get(node.connections.get(i));
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
}
