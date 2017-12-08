import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class TreePlotDisplay extends javax.swing.JFrame {
    Graph graph;
    Path nearestCluster;
    ArrayList<String> strings;

    public TreePlotDisplay(Graph graph,ArrayList<String> strings) throws IOException {
        this.graph = graph;
        initComponents();
    }

    public TreePlotDisplay(Graph graph, ArrayList<String> strings, Path path) throws IOException {
        this.graph = graph;
        nearestCluster = path;
        this.strings = strings;
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() throws IOException {

        jPanel1 = new SpanningTreePrinter(graph,strings,nearestCluster);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 953, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 652, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, Toolkit.getDefaultToolkit().getScreenSize().width, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, Toolkit.getDefaultToolkit().getScreenSize().height, Short.MAX_VALUE)
        );


        jPanel1.printerPaint();
        setVisible(true);
        pack();
    }

    private SpanningTreePrinter jPanel1;
}
