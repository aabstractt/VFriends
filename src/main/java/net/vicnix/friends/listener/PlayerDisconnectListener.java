package net.vicnix.friends.listener;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.vicnix.friends.session.SessionManager;

public class PlayerDisconnectListener implements Listener {

    @EventHandler (priority = EventPriority.NORMAL)
    public void onServerDisconnectEvent(PlayerDisconnectEvent ev) {
        SessionManager.getInstance().closeSession(ev.getPlayer());
    }
}