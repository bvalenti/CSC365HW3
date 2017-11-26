import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MyURLs {
    ArrayList<MyURL> scrapedURLS;
    String path = "C:\\CSC365HW3_BTree\\";
    String urlPath = "C:\\CSC365HW3_BTree\\myURLs.ser";
    MyURL URLS[] = new MyURL[10];

    private static MyURLs instance;
    static {
        try {
            instance = new MyURLs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MyURLs() throws IOException {
        initURLs();
    }

    public static MyURLs getInstance() {
        return instance;
    }

    //===========================================
    public void initURLs() {
        scrapedURLS = new ArrayList<>();
        URLS[0].url = "https://en.wikipedia.org/wiki/Oswego,_New_York";
        URLS[1].url = "https://en.wikipedia.org/wiki/Japanese_language";
        URLS[2].url = "https://en.wikipedia.org/wiki/Airplane";
        URLS[3].url = "https://en.wikipedia.org/wiki/Earthquake";
        URLS[4].url = "https://en.wikipedia.org/wiki/World_War_II";
        URLS[5].url = "https://en.wikipedia.org/wiki/Leonardo_da_Vinci";
        URLS[6].url = "https://en.wikipedia.org/wiki/Mathematics";
        URLS[7].url = "https://en.wikipedia.org/wiki/Rock_climbing";
        URLS[8].url = "https://en.wikipedia.org/wiki/History_of_the_United_States";
        URLS[9].url = "https://en.wikipedia.org/wiki/Karate";
    }

    //===========================================
    public void scrape() throws IOException {
        for (int i = 0; i < URLS.length; i++) {
            System.out.println(URLS[i]);
            scrapedURLS.add(URLS[i]);
            scrapeForURLS(URLS[i].url);
        }
    }

    //===========================================
    public void scrapeForURLS(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        System.out.println(doc.nodeName());
        Document root = doc;
        Elements links;
        ScrapeReturn scrapeReturn = new ScrapeReturn(doc,url);

        for (int i = 0; i < 99; i++) {
            links = scrapeReturn.doc.select("a[href]");
            scrapeReturn = recurseScrape(root, links, scrapeReturn.parentURL);
        }
    }

    //===========================================
    public ScrapeReturn recurseScrape(Document root, Elements links, String urlParent) {
        int a;
        MyURL newURL;
        Document doc;

        while (true) {
            if (links.size() >= 5) {
                a = ThreadLocalRandom.current().nextInt(1,links.size()-1);
            } else {
                links = root.select("a[href]");
                a = ThreadLocalRandom.current().nextInt(1,links.size()-1);
            }
            if (links.get(a).attr("abs:href").length() < 135 && links.get(a).attr("abs:href").length() > 5
                    && links.get(a).attr("abs:href") != null
                    && !links.get(a).attr("abs:href").contains("twitter.com")
                    && !links.get(a).attr("abs:href").contains("facebook.com")
                    && !links.get(a).attr("abs:href").contains(".jpg")
                    && !links.get(a).attr("abs:href").contains(".MP3")
                    && !links.get(a).attr("abs:href").contains(".zip")
                    && checkAscII(links.get(a).attr("abs:href"))
                    && !scrapedURLS.contains(links.get(a).attr("abs:href"))) {
                try {
                    System.out.println(links.get(a).attr("abs:href"));
                    doc = Jsoup.connect(links.get(a).attr("abs:href")).get();
                    newURL = new MyURL(links.get(a).attr("abs:href"),urlParent);
//                    scrapedURLS.add(links.get(a).attr("abs:href"));
                    scrapedURLS.add(newURL);
                    break;
                } catch (IOException e) {
                    return recurseScrape(root, links, root.nodeName());
                }
            }
        }
        return new ScrapeReturn(doc,links.get(a).attr("abs:href"));
    }

    //===========================================
    public Boolean checkAscII(String toCheck) {
        CharsetDecoder decoder = Charset.forName("US-ASCII").newDecoder();
        try {
            CharBuffer buffer = decoder.decode(ByteBuffer.wrap(toCheck.getBytes()));
        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }

    //===========================================
    public Path getFilePath(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
            url = url.replace("//", " ").replace("/", " ").replace(":", "").replace("<", "").replace(">", "").replace("*", "").replace("?", "");
        } else {
            url = url.replace("//", " ").replace("/", " ").replace(":", "").replace("<", "").replace(">", "").replace("*", "").replace("?", "");
        }
        url = url + ".ser";
        Path p = Paths.get(path, url);
        return p;
    }
}
