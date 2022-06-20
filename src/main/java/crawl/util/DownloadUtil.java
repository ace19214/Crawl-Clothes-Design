package crawl.util;

import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

@Component
public class DownloadUtil {

    public static void downloadImage(String strImageURL, String path) {
        //get file name from image path
        String strImageName =
                strImageURL.substring(strImageURL.lastIndexOf("/") + 1);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
