/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.network;

import org.json.JSONObject;
import space.itoncek.csyt.cg.logic.graphicActions.GraphicDisabledAction;
import space.itoncek.csyt.cg.logic.graphicActions.GraphicEnabledAction;
import space.itoncek.csyt.cg.logic.minigameActions.MinigameChangedAction;

import java.net.URISyntaxException;

public abstract class CGUplink {

    private final WebSocketProcessor processor;

    public CGUplink(String addr) throws URISyntaxException, InterruptedException {
        processor = new WebSocketProcessor(addr, false) {
            @Override
            public void processRequest(Action action, JSONObject payload) {
                if (action.uplink) {
                    switch (action) {
                        case GRAPHIC_ENABLED -> graphic_enabled(GraphicEnabledAction.decompile(payload));
                        case GRAPHIC_DISABLED -> graphic_disabled(GraphicDisabledAction.decompile(payload));
                        case MINIGAME_CHANGED -> minigame_changed(MinigameChangedAction.decompile(payload));
                        case DD_DOWNLINK -> decision_dome_downlink(payload);
                        case ECHO -> echo(payload);
                        case ENABLE_GRAPHIC, DISABLE_GRAPHIC -> {
                            log("Unexpected value passed thru, needs to be checked!");
                        }
                    }
                }
            }

            @Override
            public void log(String log) {
                CGUplink.this.log(log);
            }
        };
    }

    public abstract void graphic_enabled(GraphicEnabledAction action);

    public abstract void graphic_disabled(GraphicDisabledAction ac);

    public abstract void minigame_changed(MinigameChangedAction p);

    public abstract void decision_dome_downlink(JSONObject p);

    public abstract void echo(JSONObject p);

    public abstract void log(String log);

    public void enable_graphic(JSONObject payload) {
        processor.sendData(Action.ENABLE_GRAPHIC, payload);
    }

    public void disable_graphic(JSONObject payload) {
        processor.sendData(Action.DISABLE_GRAPHIC, payload);
    }
}
