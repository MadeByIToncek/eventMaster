package space.itoncek.csyt.commandportalserver;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public final class CommandPortalServer extends Plugin {
    public WebSocketServer cli;
    ProxyServer bungee;
    @Override
    public void onEnable() {
        bungee = this.getProxy();
        this.getProxy().getScheduler().schedule(this, () -> {
            cli = new WebSocketServer(InetSocketAddress.createUnresolved("localhost", 22)) {
                @Override
                public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
                    bungee.getLogger().info("Connection opened!");
                }

                @Override
                public void onClose(WebSocket webSocket, int i, String s, boolean b) {
                    bungee.getLogger().info("Connection closed!");
                }

                @Override
                public void onMessage(WebSocket webSocket, String s) {
                    cli.broadcast(s);
                    bungee.getLogger().info(s);
                }

                @Override
                public void onError(WebSocket webSocket, Exception e) {
                    bungee.getLogger().throwing("CommandPortalServer", "cli.onError()", e);
                }

                @Override
                public void onStart() {
                    bungee.getLogger().info("Ready!");
                }
            };
            cli.start();
        }, 1L, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        try {
            cli.stop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
