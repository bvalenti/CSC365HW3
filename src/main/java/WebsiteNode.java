import java.util.ArrayList;

public class WebsiteNode {
    String url;
    ArrayList<MyConnection> connections = new ArrayList<>();
    ArrayList<WebsiteNode> spanningTreeConnections = new ArrayList<>();
    WebsiteNode parent;
    String parentString;
    long marker;
    boolean visited;
    double distance;

    public WebsiteNode() {}
}
