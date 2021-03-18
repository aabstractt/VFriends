package net.vicnix.friends.session;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.vicnix.friends.VicnixFriends;
import net.vicnix.friends.translation.Translation;

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

    public Session getOfflineSession(UUID uuid) throws SessionException {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

        Session session = null;

        if (player != null) {
            session = this.getSessionPlayer(player);
        }

        if (session == null) {
            session = VicnixFriends.getInstance().getProvider().loadSession(uuid);
        }

        if (session == null) {
            throw new SessionException("Session for " + uuid.toString() + " not found");
        }

        return session;
    }

    public Session getOfflineSession(String name) throws SessionException {
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

        ProxyServer.getInstance().getLogger().info("Session opened for " + session.getName());

        this.sessions.put(player.getUniqueId().toString(), session);

        return session;
    }

    public void closeSession(ProxiedPlayer player) {
        Session session = this.getSessionPlayer(player);

        if (session == null) {
            ProxyServer.getInstance().getLogger().info("Session for " + player.getName() + " not found.");

            return;
        }

        ProxyServer.getInstance().getScheduler().runAsync(VicnixFriends.getInstance(), () -> {
            for (String uuid : session.getFriends()) {
                Session target = this.getSessionUuid(UUID.fromString(uuid));

                if (target == null) continue;

                if (!target.hasToggleNotifications()) continue;

                target.sendMessage(new TextComponent(Translation.getInstance().translateString("FRIEND_LEFT",
                        Translation.getInstance().translatePrefix(session))));
            }

            session.intentSave(true);

            ProxyServer.getInstance().getLogger().info("Closing session for " + session.getName());

            this.sessions.remove(player.getUniqueId().toString());
        });
    }
}