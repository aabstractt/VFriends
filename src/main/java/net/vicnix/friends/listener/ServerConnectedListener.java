package net.vicnix.friends.listener;

import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.vicnix.friends.session.SessionManager;

public class ServerConnectedListener implements Listener {

    @EventHandler (priority = EventPriority.NORMAL)
    public void onServerConnectedEvent(ServerConnectedEvent ev) {
        SessionManager.getInstance().createSession(ev.getPlayer());
    }
}