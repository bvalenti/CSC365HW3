public class MyURL {
    String url;
    String parentURL;

    public MyURL() {}

    public MyURL(String u) {
        url = u;
        parentURL = "";
    }

    public MyURL(String u, String p) {
        url = u;
        parentURL = p;
    }
}
