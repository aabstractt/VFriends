package net.vicnix.friends.listener;

import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.vicnix.friends.VicnixFriends;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionManager;

public class ServerDisconnectListener implements Listener {

    @EventHandler
    public void onServerDisconnectEvent(ServerDisconnectEvent ev) {
        Session session = SessionManager.getInstance().getSessionPlayer(ev.getPlayer());

        if (session == null) return;

        VicnixFriends.getInstance().getProvider().saveSession(session);
    }
}
