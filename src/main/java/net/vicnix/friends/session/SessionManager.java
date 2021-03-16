package net.vicnix.friends.session;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.vicnix.friends.VicnixFriends;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final SessionManager instance = new SessionManager();

    private final Map<String, Session> sessions = new HashMap<>();

    public static SessionManager getInstance() {
        return instance;
    }

    public Session getSessionPlayer(ProxiedPlayer player) {
        return this.getSessionUuid(player.getUniqueId());
    }

    public Session getSessionUuid(UUID uuid) {
        return this.sessions.getOrDefault(uuid.toString(), null);
    }

    public Session getSession(String name) throws SessionException {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);

        Session session = null;

        if (player != null) {
            session = this.getSessionPlayer(player);
        }

        if (session == null) {
            session = VicnixFriends.getInstance().getProvider().loadSession(name);
        }

        if (session == null) {
            throw new SessionException("Session for " + name + " not found");
        }

        return session;
    }

    public Session createSession(ProxiedPlayer player) {
        Session session = VicnixFriends.getInstance().getProvider().loadSession(player.getUniqueId(), player.getName());

        if (session == null) {
            session = new Session(player.getName(), player.getUniqueId());
        }

        this.sessions.put(player.getUniqueId().toString(), session);

        return session;
    }
}