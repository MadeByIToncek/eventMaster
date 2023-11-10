/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.logic.graphicActions;

import org.json.JSONObject;

public class GraphicEnabledAction {
    public final String graphicID;

    public GraphicEnabledAction(String graphicID) {
        this.graphicID = graphicID;
    }

    public static GraphicEnabledAction decompile(JSONObject o) {
        return new GraphicEnabledAction(o.getString("graphicID"));
    }

    public static JSONObject compile(GraphicEnabledAction action) {
        return new JSONObject().put("graphicID", action.graphicID);
    }
}