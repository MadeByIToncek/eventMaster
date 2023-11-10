/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.logic.minigameActions;

import org.json.JSONObject;
import space.itoncek.csyt.cg.common.Minigame;

public class MinigameChangedAction {
    public final Minigame source;
    public final Minigame target;

    public MinigameChangedAction(Minigame source, Minigame target) {
        this.source = source;
        this.target = target;
    }

    public static MinigameChangedAction decompile(JSONObject payload) {
        return new MinigameChangedAction(payload.getEnum(Minigame.class, "source"), payload.getEnum(Minigame.class, "target"));
    }

    public static JSONObject compile(MinigameChangedAction a) {
        return new JSONObject().put("source", a.source).put("target", a.target);
    }
}
