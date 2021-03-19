package net.vicnix.friends.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionManager;

public class ServerConnectListener implements Listener {

    @EventHandler (priority = EventPriority.NORMAL)
    public void onServerConnectEvent(ServerConnectEvent ev) {
        ProxiedPlayer player = ev.getPlayer();

        if (!ev.getTarget().getName().contains("Lobby")) return;
        
        Session session = SessionManager.getInstance().getSessionPlayer(player);
        
        if (session == null) {
            ProxyServer.getInstance().getLogger().info("Session for " + player.getName() + " not found.");

            return;
        }

        if (!session.getRequests().isEmpty()) {
            session.sendMessage(new ComponentBuilder("Tienes solicitudes de amistad pendientes! Click").color(ChatColor.GREEN)
                    .append(" AQUÍ ").color(ChatColor.LIGHT_PURPLE).bold(true)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigos pending requests"))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatColor.GREEN + "Click para ver las solicitudes pendientes.")}))
                    .append("para ver las peticiones.", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                    .create()
            );
        }
    }
}