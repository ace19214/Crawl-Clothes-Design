package crawl.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DesignByHumansService {


    public synchronized void crawlShirt(String path) {
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
                int count = 0;
                for (int i = lastPageNumber(lastPageLink); i >= 1; i--) {
                    String urlDetailWithPaging = getUrlWithPaging(urlDetail, i);
                    Document innerDocPaging = Jsoup.connect(urlDetailWithPaging).get();
                    Elements imgLinks = innerDocPaging.select("img[src]");
                    for (Element innerLink : imgLinks) {
                        String innerImgSrc = innerLink.attr("src");

                        if (!(innerImgSrc.contains("designbyhumans") && innerImgSrc.contains(".png"))) {
                            innerImgSrc = innerLink.attr("data-src");
                        }

                        Pattern pattern = Pattern.compile("(png)$", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(innerImgSrc);
                        if (matcher.find()) {
                            System.out.println("Image : " + innerImgSrc);
                            downloadImage(innerImgSrc, path);
                            count++;
                        }
                    }
                    System.out.println("COUNT = " + count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void downloadImage(String strImageURL, String path) {
        //get file name from image path
        String strImageName =
                strImageURL.substring(strImageURL.lastIndexOf("/") + 1);
        System.out.println("Saving: " + strImageName + ", from: " + strImageURL);
        try {
            //open the stream from URL
            URLConnection openConnection = new URL(strImageURL).openConnection();
            openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            InputStream in = openConnection.getInputStream();
            byte[] buffer = new byte[4096];
            int n = -1;
            OutputStream os =
                    new FileOutputStream(path + "/" + strImageName);

            //write bytes to the output stream
            while ((n = in.read(buffer)) != -1) {
                os.write(buffer, 0, n);
            }

            //close the stream
            os.close();

            System.out.println("Image saved");
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
