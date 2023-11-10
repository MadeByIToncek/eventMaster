/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.logic.housekeepingActions;

import org.json.JSONObject;
import space.itoncek.csyt.cg.logic.Processor;

public class UnimplementedAction implements Processor<UnimplementedAction> {
    @Override
    public UnimplementedAction decompile(JSONObject payload) {
        return null;
    }

    @Override
    public JSONObject compile(UnimplementedAction payload) {
        return null;
    }
}
