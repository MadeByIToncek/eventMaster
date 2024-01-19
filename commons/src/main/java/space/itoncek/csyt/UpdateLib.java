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
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

public class UpdateLib {
    public static void downloadCommitID(File pluginFolder) {
        if (!pluginFolder.exists()) pluginFolder.mkdirs();
        if (!new File(pluginFolder + "/release.id").exists()) {
            int cid = getCommitID();
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

    public static int getCommitID() {
        try {
            GHRepository repo = getRepo();
            for (GHRelease release : repo.listReleases()) {
                if (release.getTagName().equals("latest")) {
                    System.out.println(release.getId());
                    return (int) release.getId();
                }
            }
        } catch (IOException ignored) {

        }
        return -1;
    }

    public static void checkForUpdates(File pluginFolder, String plugin, File pluginFile) {
        try (Scanner sc = new Scanner(new File(pluginFolder.toString() + "/release.id"))) {
            int current = sc.nextInt();
            int remote = getCommitID();
            System.out.println("Current plugin version: " + Integer.toHexString(current) + "\tRemote plugin version: " + Integer.toHexString(remote));
            if (remote != current) {
                update(plugin, pluginFile);
                new File(pluginFolder + "/release.id").deleteOnExit();
                if (Objects.equals(plugin, "open")) {
                    System.out.println("Updating UHCCORE!");
                    new File(pluginFolder + "/../uhccore.jar").delete();
                    try (FileOutputStream fos = new FileOutputStream(pluginFolder + "/../uhccore.jar")) {
                        URLConnection con = new URL("http://cloud.itoncek.space:25574/api/maven/latest/file/releases/net/zerodind/uhccore/").openConnection();
                        fos.getChannel().transferFrom(Channels.newChannel(con.getInputStream()), 0, Long.MAX_VALUE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException | InputMismatchException e) {
            System.out.println("There is a problem with autoupdate, autoupdates are disabled!");
        }
    }

    public static void update(String plugin, File pluginFile) throws IOException {
        if (pluginFile.delete()) {
            System.out.println("Able to delete pluginFile, downloading new one to it's place");
            downloadFile(plugin, pluginFile);
        } else {
            File targetFile = getTargetFile(pluginFile);
            System.out.println("Unable to delete pluginFile, creating new file and removing the current one");
            downloadFile(plugin, targetFile);
            pluginFile.deleteOnExit();
        }
    }

    private static void downloadFile(String plugin, File targetFile) throws IOException {
        GHRepository repository = getRepo();
        String addr = "";
        for (GHRelease release : repository.listReleases()) {
            if (release.getTagName().equals("latest")) {
                for (GHAsset asset : release.listAssets()) {
                    asset.getBrowserDownloadUrl();
                    if (asset.getName().toLowerCase().contains(plugin.toLowerCase())) {
                        addr = "https://api.github.com/repos/CSYoutubeTurnaj/CSYT/releases/assets/" + asset.getId();
                    }
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
        con.setRequestProperty("Authorization", "token " + getProps().getProperty("password"));

        con.setDoInput(true);
        int status = con.getResponseCode();
        System.out.println(status);
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.getChannel().transferFrom(Channels.newChannel(con.getInputStream()), 0, Long.MAX_VALUE);
        }
        con.disconnect();
    }

    private static GHRepository getRepo() throws IOException {
        String credentials = "./config/.ghcreds";
        if (!new File(credentials).exists()) new File(credentials).createNewFile();
        GitHub gitHub = GitHubBuilder.fromPropertyFile(credentials)
                .build();
        return gitHub.getRepository("CSYoutubeTurnaj/CSYT");
    }

    private static Properties getProps() throws IOException {
        String credentials = "./config/.ghcreds";
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

    public static String getFile(String s) {
        try {
            GHRepository repo = getRepo();
            return new String(repo.getFileContent(s).read().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File downloadAssetFile(String s, File f) {
        try {
            GHRepository repo = getRepo();
            try (FileOutputStream fos = new FileOutputStream(f)) {
                fos.getChannel().transferFrom(Channels.newChannel(repo.getFileContent(s).read()), 0, Long.MAX_VALUE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return f;
    }
}
