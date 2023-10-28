/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.StringJoiner;

public class UpdateLib {
    public static void downloadCommitID(File pluginFolder) {
        if (!pluginFolder.exists()) pluginFolder.mkdirs();
        if (!new File(pluginFolder + "/release.id").exists()) {
            int cid = getCommitID();
            if (cid > 0) {
                try (FileWriter fw = new FileWriter(pluginFolder + "/release.id")) {
                    System.out.println("Cid: " + cid);
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
        try (Scanner sc = new Scanner(new URL("https://api.github.com/repos/MadeByIToncek/eventMaster/releases/tags/latest").openStream())) {
            StringJoiner js = new StringJoiner("\n");
            while (sc.hasNextLine()) js.add(sc.nextLine());
            JSONObject object = new JSONObject(js.toString());
            return object.getInt("id");
        } catch (IOException e) {
            System.out.println("Unable to fetch current tag, autoupdates are disabled!");
        }
        return -1;
    }

    public static void checkForUpdates(File pluginFolder, String plugin, File pluginFile) {
        try (Scanner sc = new Scanner(new File(pluginFolder.toString() + "/release.id"))) {
            int current = sc.nextInt();
            int remote = getCommitID();
            if (remote != current) {
                update(plugin, pluginFile);
                new File(pluginFolder.toString() + "/release.id").deleteOnExit();
            }
        } catch (IOException | InputMismatchException e) {
            System.out.println("There is a problem with autoupdate, autoupdates are disabled!");
        }
    }

    public static void update(String plugin, File pluginFile) throws IOException {
        if (pluginFile.delete()) {
            System.out.println("Able to delete pluginFile, downloading new one to it's place");
            FileUtils.copyURLToFile(
                    new URL("https://github.com/MadeByIToncek/eventMaster/releases/download/latest/%s-1.0-SNAPSHOT.jar".formatted(plugin)),
                    pluginFile,
                    3000,
                    3000);
        } else {
            File targetFile = getTargetFile(pluginFile);
            System.out.println("Unable to delete pluginFile, creating new file and removing the current one");
            FileUtils.copyURLToFile(
                    new URL("https://github.com/MadeByIToncek/eventMaster/releases/download/latest/%s-1.0-SNAPSHOT.jar".formatted(plugin)),
                    targetFile,
                    3000,
                    3000);
            pluginFile.deleteOnExit();
        }
    }

    private static File getTargetFile(File pluginFile) {
        String string = pluginFile.getAbsolutePath();
        System.out.println(string);
        String targetFileName;
        if (string.endsWith("_a.jar")) {
            targetFileName = string.substring(0, string.length() - 6) + ".jar";
        } else {
            targetFileName = string.substring(0, string.length() - 4) + "_a.jar";
        }
        System.out.println(targetFileName);
        File targetFile = new File(targetFileName);
        if (targetFile.equals(pluginFile)) {
            throw new RuntimeException("Unable to create new filename");
        }
        return targetFile;
    }
}
