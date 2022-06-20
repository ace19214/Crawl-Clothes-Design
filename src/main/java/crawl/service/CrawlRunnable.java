package crawl.service;

import crawl.util.DownloadUtil;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlRunnable implements Runnable {

    private Element innerLink;
    private String path;

    public CrawlRunnable(Element innerLink, String path) {
        this.innerLink = innerLink;
        this.path = path;
    }

    @Override
    public void run() {
        String innerImgSrc = innerLink.attr("src");

        if (!(innerImgSrc.contains("designbyhumans") && innerImgSrc.contains(".png"))) {
            innerImgSrc = innerLink.attr("data-src");
        }

        Pattern pattern = Pattern.compile("(png)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(innerImgSrc);
        if (matcher.find()) {
            DownloadUtil.downloadImage(innerImgSrc, path);
        }
    }
}
