package net.vicnix.friends.session;

import net.md_5.bungee.api.ChatColor;
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

    public Session getOfflineSession(UUID uuid) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

        Session session = null;

        if (player != null) {
            session = this.getSessionPlayer(player);
        } else {
            SessionStorage sessionStorage = VicnixFriends.getInstance().getProvider().loadSessionStorage(uuid);

            if (sessionStorage != null) {
                session = new Session(sessionStorage);
            }
        }

        if (session == null) {
            ProxyServer.getInstance().getLogger().info("Session for " + uuid.toString() + " not found");
        }

        return session;
    }

    public Session getOfflineSession(String name) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);

        Session session = null;

        if (player != null) {
            session = this.getSessionPlayer(player);
        } else {
            SessionStorage sessionStorage = VicnixFriends.getInstance().getProvider().loadSessionStorage(name);

            if (sessionStorage != null) {
                session = new Session(sessionStorage);
            }
        }

        if (session == null) {
            ProxyServer.getInstance().getLogger().info(ChatColor.RED + "Session for " + name + " not found");
        }

        return session;
    }

    public SessionStorage createSession(ProxiedPlayer player) {
        SessionStorage sessionStorage = VicnixFriends.getInstance().getProvider().loadSessionStorage(player.getUniqueId(), player.getName());

        if (sessionStorage == null) {
            sessionStorage = new SessionStorage(player.getName(), player.getUniqueId());
        }

        ProxyServer.getInstance().getLogger().info("Session opened for " + sessionStorage.getName());

        this.sessions.put(player.getUniqueId().toString(), new Session(sessionStorage));

        return sessionStorage;
    }

    public void closeSession(ProxiedPlayer player) {
        Session session = this.getSessionPlayer(player);

        if (session == null) {
            ProxyServer.getInstance().getLogger().info("Session for " + player.getName() + " not found.");

            return;
        }

        String prefix = Translation.getInstance().translatePrefix(session);

        SessionStorage sessionStorage = (SessionStorage) session.getSessionStorage().forceClone();
        Integer friendsSlots = session.getMaxFriendsSlots();

        if (sessionStorage == null) {
            ProxyServer.getInstance().getLogger().info("Session storage null");

            return;
        }

        new Thread(() -> {
            for (String uuid : sessionStorage.getFriends()) {
                Session target = this.getSessionUuid(UUID.fromString(uuid));

                if (target == null) continue;

                if (!target.getSessionStorage().hasToggleNotifications()) continue;

                target.sendMessage(new TextComponent(Translation.getInstance().translateString("FRIEND_LEFT", prefix)));
            }

            this.intentSave(sessionStorage, friendsSlots, true);

            ProxyServer.getInstance().getLogger().info("Closing session for " + session.getName());

            this.sessions.remove(sessionStorage.getUniqueId().toString());
        }).start();
    }

    public void intentSave(SessionStorage sessionStorage, Integer friendsSlots) {
        this.intentSave(sessionStorage, friendsSlots, false);
    }

    public void intentSave(SessionStorage sessionStorage, Integer friendsSlots, Boolean force) {
        if (this.getSessionUuid(sessionStorage.getUniqueId()) != null && !force) return;

        if (force) {
            VicnixFriends.getInstance().getProvider().saveSessionStorage(sessionStorage, friendsSlots);
        } else {
            ProxyServer.getInstance().getScheduler().runAsync(VicnixFriends.getInstance(), () -> VicnixFriends.getInstance().getProvider().saveSessionStorage(sessionStorage, friendsSlots));
        }
    }
}