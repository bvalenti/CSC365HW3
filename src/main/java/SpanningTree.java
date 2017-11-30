public class SpanningTree {
    WebsiteNode root;

    SpanningTree (WebsiteNode n) {
        root = n;
    }

    public boolean contains(String url) {
        return search(url, root);
    }

    public WebsiteNode get(String url) {
        return getNode(url,root);
    }

    private boolean search(String url, WebsiteNode node) {
        for (int i = 0; i < node.spanningTreeConnections.size(); i++) {
            if (node.spanningTreeConnections.get(i).url.equals(url)) {
                return true;
            }
        }
        for (int i = 0; i < node.spanningTreeConnections.size(); i++) {
            if (search(url,node.spanningTreeConnections.get(i))) {
                return true;
            }
        }
        return false;
    }

    private WebsiteNode getNode(String url, WebsiteNode node) {
        for (int i = 0; i < node.spanningTreeConnections.size(); i++) {
            if (node.spanningTreeConnections.get(i).url.equals(url)) {
                return node.spanningTreeConnections.get(i);
            }
        }
        for (int i = 0; i < node.spanningTreeConnections.size(); i++) {
            WebsiteNode out = getNode(url, node.spanningTreeConnections.get(i));
            if (out != null) {
                return out;
            }
        }
        return null;
    }
}
