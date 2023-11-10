/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg.network;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public abstract class WebSocketProcessor {
    private final WebSocketClient client;
    private final boolean direction;

    /**
     * This is the interface to talk to other websockets
     *
     * @param url       URL address of the RTL server
     * @param direction true if downlink, otherwise uplink
     * @throws URISyntaxException URL isn't valid
     */
    public WebSocketProcessor(String url, boolean direction) throws URISyntaxException, InterruptedException {
        client = new WebSocketClient(new URI(url)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                log("Server opened, " + handshakedata.getHttpStatusMessage());
            }

            @Override
            public void onMessage(String message) {
                JSONObject object = new JSONObject(message);
                if (direction == object.getBoolean("d")) {
                    Action action = object.getEnum(Action.class, "a");
                    JSONObject payload = object.getJSONObject("p");
                    processRequest(action, payload);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log("Server closed with code " + code + " (" + reason + ")");
            }

            @Override
            public void onError(Exception ex) {
                log("[ERR] " + Arrays.toString(ex.getStackTrace()));
            }
        };
        client.connectBlocking();
        this.direction = direction;
    }

    public abstract void processRequest(Action action, JSONObject payload);

    public abstract void log(String log);

    public void sendData(Action a, JSONObject p) {
        JSONObject object = new JSONObject();
        object.put("d", !direction);
        object.put("a", a);
        object.put("p", p);
        client.send(object.toString());
    }
}
