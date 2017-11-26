import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;

public class MainGUI extends javax.swing.JFrame {
    GridBagConstraints gbc;
    BNode root;
    int nodeCount;
    MyUtility utl = MyUtility.getInstance();
    HTMLParser parser = new HTMLParser();
    Object lock = new Object();
    Object medoidLock = new Object();
    static String BTreePath = "C:\\CSC365HW3_BTree\\btree.ser";
    static String RootIDPath = "C:\\CSC365HW3_BTree\\rootID.ser";

    public MainGUI() throws IOException {}

    @SuppressWarnings("unchecked")
    private void initComponents() { }


    private void addComponents(Container pane) throws IOException {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                FileOutputStream f;
                ObjectOutputStream os;
                String fileKeyLastModified = "";
                Medoid medoids[] = null;

                try {
                    fileKeyLastModified = utl.fetch(null,lock,"oldest file");
                    medoids = utl.readClusters(medoidLock);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < medoids.length; i++) {
                    if (medoids[i].key.equals(fileKeyLastModified)) {
                        try {
                            synchronized (medoidLock) {
                                Path filePath = utl.getFilePath(medoids[i].key);
                                File medoidFilePath = filePath.toFile();
                                FrequencyTable newFreqs = parser.parseURL(medoids[i].key);
                                f = new FileOutputStream(medoidFilePath);
                                os = new ObjectOutputStream(f);
                                newFreqs.frequencies.writeObject(os);
                                os.close();
                                f.close(); }
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }

                Path filePath = utl.getFilePath(fileKeyLastModified);
                File fileLastModified = filePath.toFile();

                try {
                    synchronized(lock) {
                        FrequencyTable newFreqs = parser.parseURL(fileKeyLastModified);
                        f = new FileOutputStream(fileLastModified);
                        os = new ObjectOutputStream(f);
                        newFreqs.frequencies.writeObject(os);
                        os.close();
                        f.close();
                    }
                    System.out.println("I updated a frequency table!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },30000,30000);

        Font font1 = new Font("SansSerif", Font.BOLD, 35);
        jButton1 = new javax.swing.JButton();
        jButton1.setText("Find URL");
        jButton1.setFont(font1);
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
        pane.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.insets = new Insets(0,30,0,0);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 4;
        pane.add(jTextField1, gbc);
        jTextField1.setFont(font1);

        gbc.insets = new Insets(0,0,0,30);
        gbc.gridx = 4; gbc.gridy = 0;
        gbc.gridwidth = 1;
        pane.add(jButton1, gbc);

        gbc.insets = new Insets(0,30,0,0);
        gbc.gridx = 0; gbc.gridy = 1;
        pane.add(jLabel5, gbc);
        jLabel5.setText("Most Similar:");
        jLabel5.setFont(font1);

        gbc.gridx = 0; gbc.gridy = 2;
        pane.add(jLabel6, gbc);
        jLabel6.setText("Category:");
        jLabel6.setFont(font1);

        root = new BNode(BTree.childNum);
        long rootId = getRootID();
        System.out.println(rootId);
        root.readNode(rootId);
        nodeCount = getNodeCount();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) throws IOException, ClassNotFoundException {
        String url = jTextField1.getText();
        String tmpUrl;
        FrequencyTable freq;
        FileOutputStream f;
        ObjectOutputStream os;
        double sim = 0;
        double tmpSim;
        String mostSimilar = "";
        String category = "";
        double simMetrics[] = new double[1000];
        String urls[] = new String[1000];
        int index = -1;
        int count = 0;

        HTMLParser urlParser = new HTMLParser();
        FrequencyTable urlFreq = urlParser.parseURL(url);

        mostSimilar = utl.fetch(url,lock,"most similar");
        category = utl.getClusterCategory(urlFreq, medoidLock);
        jLabel5.setText("Most similar url: " + mostSimilar);
        jLabel6.setText("Category: " + category);
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainGUI frame = null;
                try {
                    frame = new MainGUI();
                    frame.addComponents(frame.getContentPane());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                frame.setSize(2000,500);
                frame.setVisible(true);
            }
        });

    }

    public long getRootID() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(RootIDPath, "rw");
        FileChannel f = raf.getChannel();
        ByteBuffer b = ByteBuffer.allocate(8);
        f.read(b);
        b.position(0);
        long id = b.getLong();
        return id;
    }

    public int getNodeCount() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(BTreePath, "rw");
        raf.seek(8);
        FileChannel f = raf.getChannel();
        ByteBuffer b = ByteBuffer.allocate(4);
        f.read(b);
        b.position(0);
        int id = b.getInt();
        return id;
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
}
