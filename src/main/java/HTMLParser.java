import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HTMLParser {

    public FrequencyTable parseURL(String url) {
        FrequencyTable out = new FrequencyTable();

        try {
            Document doc = Jsoup.connect(url).get();
            Elements metaTags = doc.getElementsByTag("meta");
            Elements elements = doc.select("*");

            ArrayList<String> commonWords = readCommonWords();

            for (Element element : elements) {
                String tmp[] = element.ownText().split(" ");
                for (int j = 0; j < tmp.length; j++) {
                    if (!tmp[j].equals("")) {
                        tmp[j] = tmp[j].replaceAll("\\P{L}+", "");
                        if (!commonWords.contains(tmp[j])) {
                            out.frequencies.put(tmp[j], 1);
                        }
                    }
                }
            }

            for (Element element : metaTags) {
                String tmp[] = element.attr("content").split(" |,|\\.|-|:|=|;|/|\\?|!|%|\\(|\\)|@");
                for (int j = 0; j < tmp.length; j++) {
                    tmp[j] = tmp[j].replaceAll("\\P{L}+", "");
                    if (!commonWords.contains(tmp[j])) {
                        out.frequencies.putMeta(tmp[j], 1);
                    }
                }
            }
        } catch (IOException e) {
            return parseURL(url);
        }
        return out;
    }

    public ArrayList<String> readCommonWords() throws IOException {
        ArrayList<String> commonWords = new ArrayList<>();
        String csvFile = "C:\\CSC365_BTree\\common_words.csv";
        BufferedReader br = null;
        String line = "";

        br = new BufferedReader(new FileReader(csvFile));
        while ((line = br.readLine()) != null) {
            String[] wordLine = line.split(",");
            commonWords.add(wordLine[0]);
        }
        return commonWords;
    }
}
