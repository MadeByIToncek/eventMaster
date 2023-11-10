/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.logic.graphicActions;

import org.json.JSONObject;

public class EnableGraphicAction {
    public final String graphicID;

    public EnableGraphicAction(String graphicID) {
        this.graphicID = graphicID;
    }

    public static EnableGraphicAction decompile(JSONObject o) {
        return new EnableGraphicAction(o.getString("graphicID"));
    }

    public static JSONObject compile(EnableGraphicAction action) {
        return new JSONObject().put("graphicID", action.graphicID);
    }
}
