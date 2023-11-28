/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt;

import org.kohsuke.github.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

public class UpdateLib {
    public static void downloadCommitID(File pluginFolder, String cf) {
        if (!pluginFolder.exists()) pluginFolder.mkdirs();
        if (!new File(pluginFolder + "/release.id").exists()) {
            int cid = getCommitID(cf);
            System.out.println("Current plugin version: " + Integer.toHexString(cid));
            if (cid > 0) {
                try (FileWriter fw = new FileWriter(pluginFolder + "/release.id")) {
                    System.out.println("Version ID: " + Integer.toHexString(cid));
                    fw.write(cid + "");
                } catch (IOException e) {
                    System.out.println("Unable to fetch current tag, autoupdates are disabled!");
                }
            } else {
                System.out.println("Unable to fetch current tag, autoupdates are disabled!");
            }

        }
    }

    public static int getCommitID(String cf) {
        try {
            GHRepository repo = getRepo(cf);
            return (int) repo.getLatestRelease().getId();
        } catch (IOException ignored) {
        }
        return -1;
    }

    public static void checkForUpdates(File pluginFolder, String plugin, File pluginFile, String credentials) {
        try (Scanner sc = new Scanner(new File(pluginFolder.toString() + "/release.id"))) {
            int current = sc.nextInt();
            int remote = getCommitID(credentials);
            System.out.println("Current plugin version: " + Integer.toHexString(current) + "\tRemote plugin version: " + Integer.toHexString(remote));
            if (remote != current) {
                update(plugin, pluginFile, credentials);
                new File(pluginFolder + "/release.id").deleteOnExit();
            }
        } catch (IOException | InputMismatchException e) {
            System.out.println("There is a problem with autoupdate, autoupdates are disabled!");
        }
    }

    public static void update(String plugin, File pluginFile, String credentials) throws IOException {
        if (pluginFile.delete()) {
            System.out.println("Able to delete pluginFile, downloading new one to it's place");
            downloadFile(plugin, pluginFile, credentials);
        } else {
            File targetFile = getTargetFile(pluginFile);
            System.out.println("Unable to delete pluginFile, creating new file and removing the current one");
            downloadFile(plugin, targetFile, credentials);
            pluginFile.deleteOnExit();
        }
    }

    private static void downloadFile(String plugin, File targetFile, String credentials) throws IOException {
        GHRepository repository = getRepo(credentials);
        String addr = "";
        for (GHRelease release : repository.listReleases()) {
            System.out.println("release: " + release.getName());
            System.out.println("assets :");
            for (GHAsset asset : release.listAssets()) {
                asset.getBrowserDownloadUrl();
                if (asset.getName().toLowerCase().contains(plugin.toLowerCase())) {
                    addr = "https://api.github.com/repos/CSYoutubeTurnaj/CSYT/releases/assets/" + asset.getId();
                }
            }
        }
        if (addr.isEmpty()) {
            System.out.println("Unable find this plugin!");
        }

        URL url = new URL(addr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/octet-stream");
        con.setRequestProperty("Authorization", "token " + getProps(credentials).getProperty("password"));

        con.setDoInput(true);
        int status = con.getResponseCode();
        System.out.println(status);
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.getChannel().transferFrom(Channels.newChannel(con.getInputStream()), 0, Long.MAX_VALUE);
        }
        con.disconnect();
    }

    private static GHRepository getRepo(String credentials) throws IOException {
        if (!new File(credentials).exists()) new File(credentials).createNewFile();
        GitHub gitHub = GitHubBuilder.fromPropertyFile(credentials)
                .withRateLimitHandler(RateLimitHandler.FAIL)
                .build();
        return gitHub.getRepository("CSYoutubeTurnaj/CSYT");
    }

    private static Properties getProps(String credentials) throws IOException {
        if (!new File(credentials).exists()) new File(credentials).createNewFile();
        Properties props = new Properties();
        props.load(new FileInputStream(credentials));
        return props;
    }

    private static File getTargetFile(File pluginFile) {
        String string = pluginFile.getAbsolutePath();
        //System.out.println(string);
        String targetFileName;
        if (string.endsWith("_a.jar")) {
            targetFileName = string.substring(0, string.length() - 6) + ".jar";
        } else {
            targetFileName = string.substring(0, string.length() - 4) + "_a.jar";
        }
        //System.out.println(targetFileName);
        File targetFile = new File(targetFileName);
        if (targetFile.equals(pluginFile)) {
            throw new RuntimeException("Unable to create new filename");
        }
        return targetFile;
    }
}
