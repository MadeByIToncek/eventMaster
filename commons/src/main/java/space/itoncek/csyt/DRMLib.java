package space.itoncek.csyt;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class DRMLib {
    public static boolean checkDRM() {
        try {
            URL url = new URL("http://drmblock.itoncek.space");
            URLConnection urlConnection = url.openConnection();

            urlConnection.connect();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}