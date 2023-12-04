/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.open.cfg;

import fun.csyt.open.meta.TeamMeta;
import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class CFGMGR {

    @Deprecated(since = "removing /assign command", forRemoval = true)
    public static @Nullable HashMap<TeamMeta, List<String>> readTeams(File datadir) {
        if (configNotPresent(datadir)) {
            mkdirs(datadir);
            dumpDefaultCFG(datadir);
        }
        File cfg = new File(datadir + "/config.json");
        JSONObject raw = new JSONObject();

        try (Scanner sc = new Scanner(cfg)) {
            StringJoiner js = new StringJoiner("\n");
            while (sc.hasNextLine()) {
                js.add(sc.nextLine());
            }
            raw = new JSONObject(js.toString());
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }


        HashMap<TeamMeta, List<String>> out = new HashMap<>();

        for (int i = 0; i < raw.length(); i++) {
            JSONObject team = raw.getJSONArray("teams").getJSONObject(i);
            TeamMeta meta = new TeamMeta(team.getString("id"), team.getString("icon"), team.getBoolean("spectator"));
            List<String> players = new ArrayList<>();

            for (Object o : team.getJSONArray("players")) {
                String player = (String) o;
                players.add(player);
            }
            out.put(meta, players);
        }

        return out;
    }

//    public static @Nullable String readDBURL(File datadir) {
//        if (!checkIfConfigIsPresent(datadir)) {
//            mkdirs(datadir);
//            dumpDefaultCFG(datadir);
//        }
//        File cfg = new File(datadir + "/config.json");
//        JSONObject raw = new JSONObject();
//
//        try (Scanner sc = new Scanner(cfg)) {
//            StringJoiner js = new StringJoiner("\n");
//            while (sc.hasNextLine()) {
//                js.add(sc.nextLine());
//            }
//            raw = new JSONObject(js.toString());
//        } catch (FileNotFoundException e) {
//            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
//        }
//        return raw.getString("dburl");
//    }

    public static boolean configNotPresent(File datadir) {
        return !new File(datadir + "/config.json").exists();
    }

    private static void dumpDefaultCFG(File datadir) {
        File cfg = new File(datadir + "/config.json");
        try (FileWriter fw = new FileWriter(cfg)) {
            fw.write(new JSONObject().put("dburl", "").put("dbcToken", new JSONArray().put("token1").put("token2")).put("dbcGuildID", 0L).put("mainVoice", 0L).put("dbcCategoryID", 0L).toString(4));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static void mkdirs(File datadir) {
        datadir.mkdirs();
    }

    public static JSONObject getConfig(File datadir) {
        if (configNotPresent(datadir)) {
            mkdirs(datadir);
            dumpDefaultCFG(datadir);
        }
        File cfg = new File(datadir + "/config.json");
        JSONObject raw = new JSONObject();

        try (Scanner sc = new Scanner(cfg)) {
            StringJoiner js = new StringJoiner("\n");
            while (sc.hasNextLine()) {
                js.add(sc.nextLine());
            }
            raw = new JSONObject(js.toString());
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
        return raw;
    }
}