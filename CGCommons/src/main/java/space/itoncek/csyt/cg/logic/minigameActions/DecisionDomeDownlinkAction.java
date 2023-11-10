/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.logic.minigameActions;

import org.json.JSONArray;
import org.json.JSONObject;
import space.itoncek.csyt.cg.common.Location;
import space.itoncek.csyt.cg.common.Minigame;
import space.itoncek.csyt.cg.logic.Processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecisionDomeDownlinkAction implements Processor<DecisionDomeDownlinkAction> {
    public final HashMap<String, Location> data;
    public final int remaining;
    public final List<Minigame> minigameMask;

    public DecisionDomeDownlinkAction(HashMap<String, Location> data, int remaining, List<Minigame> minigameMask) {
        this.data = data;
        this.remaining = remaining;
        this.minigameMask = minigameMask;
    }

    public static int compileMinigameMask(List<Minigame> minigames) {
        int out = 0b0;
        for (Minigame minigame : minigames) {
            out = out | minigame.mask;
        }
        return out;
    }

    public static List<Minigame> decompileMinigameMask(int input) {
        List<Minigame> minigames = new ArrayList<>();
        for (Minigame value : Minigame.values()) {
            if ((value.mask & input) > 0) {
                minigames.add(value);
            }
        }
        return minigames;
    }

    @Override
    public DecisionDomeDownlinkAction decompile(JSONObject payload) {
        return new DecisionDomeDownlinkAction(deconvert(payload.getJSONArray("players")), payload.getInt("rem"), decompileMinigameMask(payload.getInt("mgm")));
    }

    private HashMap<String, Location> deconvert(JSONArray players) {
        HashMap<String, Location> out = new HashMap<>();
        for (Object player : players) {
            JSONObject obj = (JSONObject) player;
            out.put(obj.getString("n"), Location.parseLocation(obj.getString("l")));
        }
        return out;
    }

    @Override
    public JSONObject compile(DecisionDomeDownlinkAction action) {
        return new JSONObject().put("rem", action.remaining).put("players", convert(action.data)).put("mgm", compileMinigameMask(action.minigameMask));
    }

    private JSONArray convert(HashMap<String, Location> data) {
        JSONArray arr = new JSONArray();
        for (Map.Entry<String, Location> entry : data.entrySet()) {
            JSONObject o = new JSONObject();
            o.put("n", entry.getKey());
            o.put("l", entry.getValue().toString());
        }
        return arr;
    }
}
