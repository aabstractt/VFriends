package net.vicnix.friends.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.vicnix.friends.VicnixFriends;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionManager;
import net.vicnix.friends.translation.Translation;

import java.util.UUID;

public class ServerConnectedListener implements Listener {

    @EventHandler (priority = EventPriority.NORMAL)
    public void onServerConnectedEvent(ServerConnectedEvent ev) {
        ProxyServer.getInstance().getScheduler().runAsync(VicnixFriends.getInstance(), () -> {
            Session session = SessionManager.getInstance().createSession(ev.getPlayer());

            for (String uuid : session.getFriends()) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));

                if (player == null) continue;

                player.sendMessage(new TextComponent(Translation.getInstance().translateString("FRIEND_JOINED", player.getName())));
            }
        });
    }
}