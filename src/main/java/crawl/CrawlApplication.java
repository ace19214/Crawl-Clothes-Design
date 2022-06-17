package crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class CrawlApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlApplication.class, args);
        crawlShirt();

    }

    private synchronized static void crawlShirt() {
        List<String> linkCrawl = new ArrayList<>();
        linkCrawl.add("StarWars");
        linkCrawl.add("marvel");
        linkCrawl.add("mens-t-shirts");
        linkCrawl.add("womens-t-shirts");
        linkCrawl.add("best-new-t-shirts");
        linkCrawl.add("DCComics");
        linkCrawl.add("Retrofuture-Collection");
        linkCrawl.add("featured-comics");
        linkCrawl.add("featured-gaming");
        linkCrawl.add("featured-horror");
        linkCrawl.add("featured-cartoons");
        linkCrawl.add("80s-collection");

        final String PREFIX = "/shop/";

        Set<String> linkWebsite = new HashSet<>();

        Document doc;
        String url = "https://www.designbyhumans.com";
        try {
            doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String innerurl = link.attr("href");
                if (innerurl.contains(PREFIX)) {
                    linkWebsite.add(innerurl);
                }
            }

            for (String innerurl : linkWebsite) {

                String[] tokenUrl = innerurl.split("/");

                if (tokenUrl.length > 1 && !linkCrawl.contains(tokenUrl[2])) {
                    continue;
                }

                System.out.println("*******");
                System.out.println(innerurl);
                String urlDetail = url + innerurl;

                Document innerDoc = Jsoup.connect(urlDetail).get();
                Elements imgLinks = innerDoc.select("img[src]");

                int count = 0;
                for (Element innerLink : imgLinks) {
                    String innerImgSrc = innerLink.attr("src");

                    if (!(innerImgSrc.contains("designbyhumans") && innerImgSrc.contains(".png"))) {
                        innerImgSrc = innerLink.attr("data-src");

                    }
                    Pattern pattern = Pattern.compile("(png)$", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(innerImgSrc);
                    if (matcher.find()) {
                        System.out.println("Image : " + innerImgSrc);
                        count++;
                    }
                }
                System.out.println("COUNT = " +count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
