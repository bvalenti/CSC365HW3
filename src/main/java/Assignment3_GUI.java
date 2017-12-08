import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class Assignment3_GUI extends javax.swing.JFrame {
    ArrayList<String> strings = new ArrayList<>();
    DefaultListModel<String> model = new DefaultListModel<>();
    int indexSelectedElement = 0;
    Graph graph = new Graph();

        public Assignment3_GUI() throws IOException, ClassNotFoundException {
            initComponents();
        }

        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() throws IOException {
            BNode root = new BNode(8);
            long rootID = BTree.readRootId();
            root.readNode(rootID);

            jScrollPane1 = new javax.swing.JScrollPane();
            jList1 = new javax.swing.JList<>();
            initListModel(root);

            ListSelectionModel listSelectionModel = jList1.getSelectionModel();
            listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());

            jList1.setModel(model);
            jButton1 = new javax.swing.JButton();
            jLabel1 = new javax.swing.JLabel();
            jButton2 = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

            jScrollPane1.setViewportView(jList1);

            jButton1.setText("Run");
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        jButton1ActionPerformed(evt);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            jButton2.setText("Find Number of Spanning Trees");
            jButton2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        jButton2ActionPerformed(evt);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            jLabel1.setBackground(java.awt.SystemColor.controlLtHighlight);
            jLabel1.setText(" ");

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(jButton1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap())
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                                    .addGap(23, 23, 23)
                                    .addComponent(jLabel1)
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jButton1)
                                            .addComponent(jButton2)))
            );

//            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
//            getContentPane().setLayout(layout);
//            layout.setHorizontalGroup(
//                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                            .addComponent(jScrollPane1)
//                            .addGroup(layout.createSequentialGroup()
//                                    .addComponent(jButton1)
//                                    .addGap(0, 0, Short.MAX_VALUE))
//                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
//            );
//            layout.setVerticalGroup(
//                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                            .addGroup(layout.createSequentialGroup()
//                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
//                                    .addGap(23, 23, 23)
//                                    .addComponent(jLabel1)
//                                    .addGap(18, 18, 18)
//                                    .addComponent(jButton1))
//            );

            pack();
        }// </editor-fold>

        /**
         * @param args the command line arguments
         */
        public static void main(String args[]) {
        /* Set the Nimbus look and feel */
            //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(Assignment3_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(Assignment3_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(Assignment3_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(Assignment3_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            //</editor-fold>

        /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        Assignment3_GUI newGUI = new Assignment3_GUI();
                        newGUI.setSize(Toolkit.getDefaultToolkit().getScreenSize().width/3,Toolkit.getDefaultToolkit().getScreenSize().height*35/64);
                        newGUI.setVisible(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    public void initListModel(BNode r) throws IOException {
            int count = 0;
        getStrings(r);
        strings.sort(new CompareStringsLexicographically());
        for (String s : strings) {
            model.addElement(Integer.toString(count) + ". " + s);
            count++;
        }
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) throws IOException, ClassNotFoundException {
        String url = strings.get(indexSelectedElement);
        graph.readNodes();
        Path nearestCluster = graph.findNearestClusterCenter(url);
        jLabel1.setText("Nearest Cluster Center: " + nearestCluster.dst.url);
        TreePlotDisplay display = new TreePlotDisplay(graph,strings,nearestCluster);
        ClosestClusterCenterPopup popup = new ClosestClusterCenterPopup(nearestCluster.dst.url);
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) throws IOException, ClassNotFoundException {
        String url = strings.get(indexSelectedElement);
        graph.readNodes();
        int numberOfSpanningTrees = graph.findNumberOfSpanningTrees();
        jLabel1.setText("Number of Spanning Trees: " + Integer.toString(numberOfSpanningTrees));
//        NumberOfSpanningTreesPrompt numberOfSpanningTreesPrompt = new NumberOfSpanningTreesPrompt(graph,strings);
    }

    private void getStrings(BNode r) throws IOException {
        BNode e;
        for (int i = 0; i < r.children.length; i++) {
            if (r.children[i] != 0) {
                e = new BNode(8);
                e.readNode(r.children[i]);
                getStrings(e);
            }
        }
        for (int i = 0; i < r.keys.length; i++) {
            if (r.keys[i] != null) {
                strings.add(r.keys[i]);
            }
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration


    class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (lsm.isSelectionEmpty()) {
            } else {
                indexSelectedElement = lsm.getMaxSelectionIndex();
            }
        }
    }
}

class CompareStringsLexicographically implements Comparator<String> {
    public int compare(String a, String b) {
        return a.compareToIgnoreCase(b);
    }
}
