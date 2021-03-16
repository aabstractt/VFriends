package net.vicnix.friends.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
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

            if (!session.getRequests().isEmpty()) {
                session.sendMessage(new ComponentBuilder("Tienes solicitudes de amistad pendientes! Click").color(ChatColor.GREEN)
                        .append(" AQUÍ ").color(ChatColor.LIGHT_PURPLE).bold(true)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigos pending requests"))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatColor.GREEN + "Click para ver las solicitudes pendientes.")}))
                        .append("para ver las peticiones.", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                        .create());
            }

            for (String uuid : session.getFriends()) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));

                if (player == null) continue;

                player.sendMessage(new ComponentBuilder(Translation.getInstance().translateString("FRIEND_LISTENER_PREFIX"))
                        .append(session.getName()).color(ChatColor.GRAY)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party invite " + session.getName()))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click para enviar invitacion de party").color(ChatColor.GREEN).create()))
                        .append(Translation.getInstance().translateString("FRIEND_JOINED"), ComponentBuilder.FormatRetention.NONE)
                        .create()
                );
            }
        });
    }
}