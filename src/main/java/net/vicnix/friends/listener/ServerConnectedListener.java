package net.vicnix.friends.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionManager;

import java.util.UUID;

public class ServerConnectedListener implements Listener {

    @EventHandler (priority = EventPriority.NORMAL)
    public void onServerConnectedEvent(ServerConnectedEvent ev) {
        Session session = SessionManager.getInstance().createSession(ev.getPlayer());

        for (String uuid : session.getFriends()) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));

            if (player == null) continue;

            player.sendMessage("Tu amigo " + session.getName() + " se ha conectado.");
        }
    }
}