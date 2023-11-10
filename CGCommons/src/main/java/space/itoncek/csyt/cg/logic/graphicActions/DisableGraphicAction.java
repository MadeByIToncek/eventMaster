/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.logic.graphicActions;

import org.json.JSONObject;

public class DisableGraphicAction {
    public final String graphicID;

    public DisableGraphicAction(String graphicID) {
        this.graphicID = graphicID;
    }

    public static DisableGraphicAction decompile(JSONObject o) {
        return new DisableGraphicAction(o.getString("graphicID"));
    }

    public static JSONObject compile(DisableGraphicAction action) {
        return new JSONObject().put("graphicID", action.graphicID);
    }
}
