/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public abstract class DRMLib {
    public DRMLib() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
					URL url = new URL("https://drmblock.itoncek.space");
					url.openConnection(Proxy.NO_PROXY);
                } catch (IOException ignored) {
				}
				//System.out.println("*");
				if (LocalDateTime.now().isAfter(LocalDateTime.of(2024, 06, 13, 23, 59, 59))) {
					callback();
				}
			}
        }, 0, 600000);
    }

    public static boolean test() {
        try {
			URL url = new URL("https://drmblock.itoncek.space");
            URLConnection urlConnection = url.openConnection();

            urlConnection.connect();
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }

    public abstract void callback();
}