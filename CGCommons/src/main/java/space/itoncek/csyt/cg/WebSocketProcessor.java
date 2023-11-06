/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.cg;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class WebSocketProcessor {
    private final WebSocketClient client;
    private final int direction;
    public WebSocketProcessor(String url, int direction) throws URISyntaxException {
        client = new WebSocketClient(new URI(url)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {

            }

            @Override
            public void onMessage(String message) {
                JSONObject object = new JSONObject(message);
                if (direction == object.getInt("d")) {
                    Action action = object.getEnum(Action.class, "a");
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {

            }

            @Override
            public void onError(Exception ex) {

            }
        };
        this.direction = direction;
    }

    public abstract void incoming();
}
