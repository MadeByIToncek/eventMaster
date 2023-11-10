/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.logic.graphicActions;

import org.json.JSONObject;

public class GraphicDisabledAction {
    public final String graphicID;

    public GraphicDisabledAction(String graphicID) {
        this.graphicID = graphicID;
    }

    public static GraphicDisabledAction decompile(JSONObject o) {
        return new GraphicDisabledAction(o.getString("graphicID"));
    }

    public static JSONObject compile(GraphicDisabledAction action) {
        return new JSONObject().put("graphicID", action.graphicID);
    }
}
