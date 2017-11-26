import org.jsoup.nodes.Document;

public class ScrapeReturn {
    Document doc;
    String parentURL;

    ScrapeReturn(Document d, String p) {
        doc = d;
        parentURL = p;
    }
}
