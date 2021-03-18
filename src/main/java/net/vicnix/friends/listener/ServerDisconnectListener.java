package net.vicnix.friends.listener;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.vicnix.friends.session.SessionManager;

public class ServerDisconnectListener implements Listener {

    @EventHandler
    public void onServerDisconnectEvent(PlayerDisconnectEvent ev) {
        SessionManager.getInstance().closeSession(ev.getPlayer());
    }
}