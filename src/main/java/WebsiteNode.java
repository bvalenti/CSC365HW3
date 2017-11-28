import java.util.ArrayList;

public class WebsiteNode {
    String url;
    ArrayList<MyConnection> connections = new ArrayList<>();
    ArrayList<WebsiteNode> spanningTreeConnections = new ArrayList<>();
    WebsiteNode parent;
    long marker;
    boolean visited;

    public WebsiteNode() {}
}
