package crawl.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DesignByHumansService {

    public void crawlShirt(String path, int fromNum, int toNum) {
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
                Elements pagingLinks = innerDoc.select("a[data-id]");
                Element lastPageLink = pagingLinks.get(pagingLinks.size() - 2);
                int maxPageNumber = lastPageNumber(lastPageLink);

                if (maxPageNumber < fromNum) {
                    continue;
                }

                if (maxPageNumber < toNum) {
                    toNum = maxPageNumber;
                }

                for (int i = fromNum; i <= toNum; i++) {
                    String urlDetailWithPaging = getUrlWithPaging(urlDetail, i);
                    Document innerDocPaging = Jsoup.connect(urlDetailWithPaging).get();
                    Elements imgLinks = innerDocPaging.select("img[src]");
                    System.out.println("page number: " + i);
                    for (Element innerLink : imgLinks) {
                        Runnable crawlRunnable = new CrawlRunnable(innerLink, path);
                        new Thread(crawlRunnable).start();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int lastPageNumber(Element lastPagingLink) {
        int lastPageNumber = 0;
        try {
            lastPageNumber = Integer.parseInt(lastPagingLink.childNode(0).toString());
        } catch (NumberFormatException e) {
            System.out.println("Something's wrong when get lastPageNumber" + e.getMessage());
        }
        return lastPageNumber;
    }

    private String getUrlWithPaging(String url, int pageNumber) {
        return url + "page/" + pageNumber + "/?hs=true";
    }
}
