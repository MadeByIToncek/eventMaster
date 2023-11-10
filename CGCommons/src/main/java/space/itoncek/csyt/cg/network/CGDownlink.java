/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.network;

import org.json.JSONObject;

import java.net.URISyntaxException;

public abstract class CGDownlink {
    private final WebSocketProcessor processor;

    public CGDownlink(String addr) throws URISyntaxException, InterruptedException {
        processor = new WebSocketProcessor(addr, true) {
            @Override
            public void processRequest(Action action, JSONObject payload) {
                if (action.uplink) {
                    switch (action) {
                        case GRAPHIC_ENABLED, GRAPHIC_DISABLED, MINIGAME_CHANGED, DD_DOWNLINK -> {
                            log("Unexpected value passed thru, needs to be checked!");
                        }
                        case ECHO -> {
                            echo(payload);
                        }
                        case ENABLE_GRAPHIC -> {
                            enable_graphic(payload);
                        }
                        case DISABLE_GRAPHIC -> {
                            disable_graphic(payload);
                        }
                    }
                }
            }

            @Override
            public void log(String log) {
                CGDownlink.this.log(log);
            }
        };
    }

    public abstract void enable_graphic(JSONObject p);

    public abstract void disable_graphic(JSONObject p);

    public abstract void echo(JSONObject p);

    public abstract void log(String log);

    public void graphic_enabled(JSONObject data) {
        processor.sendData(Action.GRAPHIC_ENABLED, data);
    }

    public void graphic_disabled(JSONObject data) {
        processor.sendData(Action.GRAPHIC_DISABLED, data);
    }

    public void minigame_changed(JSONObject data) {
        processor.sendData(Action.MINIGAME_CHANGED, data);
    }

    public void dd_downlink(JSONObject data) {
        processor.sendData(Action.DD_DOWNLINK, data);
    }
}
