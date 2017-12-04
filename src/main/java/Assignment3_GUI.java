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
            graph.readNodes();
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



            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(jButton1)
                                    .addGap(0, 403, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addContainerGap(136, Short.MAX_VALUE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(55, 55, 55)
                                    .addComponent(jButton1))
            );

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
                        newGUI.setSize(Toolkit.getDefaultToolkit().getScreenSize().width/3,Toolkit.getDefaultToolkit().getScreenSize().height/2);
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
        getStrings(r);
        strings.sort(new CompareStringsLexicographically());
        for (String s : strings) {
            model.addElement(s);
        }
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) throws IOException, ClassNotFoundException {
        String url = strings.get(indexSelectedElement);
        graph.findNearestClusterCenter(url);
        TreePlotDisplay display = new TreePlotDisplay(graph);
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
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
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
