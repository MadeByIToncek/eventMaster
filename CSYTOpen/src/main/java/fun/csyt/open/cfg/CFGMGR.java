/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package fun.csyt.open.cfg;

import fun.csyt.open.meta.TeamMeta;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static fun.csyt.open.CSYTOpen.log;

public class CFGMGR {
    public CFGMGR(File datadir) {
        boolean present = checkIfConfigIsPresent(datadir);
        if (!present) {
            mkdirs(datadir);
            dumpDefaultCFG(datadir);
        }
    }

    public static @Nullable HashMap<TeamMeta, List<String>> readTeams(File datadir) {
        if (checkIfConfigIsPresent(datadir)) {
            File cfg = new File(datadir + "/config.json");
            JSONArray raw = new JSONArray();

            try (Scanner sc = new Scanner(cfg)) {
                StringJoiner js = new StringJoiner("\n");
                while (sc.hasNextLine()) {
                    js.add(sc.nextLine());
                }
                raw = new JSONArray(js.toString());
            } catch (FileNotFoundException e) {
                log.throwing("CFGMGR", "readTeams()", e);
            }


            HashMap<TeamMeta, List<String>> out = new HashMap<>();

            for (int i = 0; i < raw.length(); i++) {
                JSONObject team = raw.getJSONObject(i);
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
        return null;
    }

    public static boolean checkIfConfigIsPresent(File datadir) {
        return new File(datadir + "/config.json").exists();
    }

    private void dumpDefaultCFG(File datadir) {
        File cfg = new File(datadir + "/config.json");
        try (FileWriter fw = new FileWriter(cfg)) {
            fw.write((new JSONArray().put(new JSONObject().put("id", "coal").put("icon", "⫽").put("spectator", true).put("players", new JSONArray()
                    .put("IToncek")
                    .put("NeXuSoveVidea")
                    .put("mrkwi")))
            ).toString(4));
        } catch (IOException e) {
            log.throwing("CFGMGR", "dumpDefaultCFG()", e);
        }
    }

    private void mkdirs(File datadir) {
        datadir.mkdirs();
    }
}
