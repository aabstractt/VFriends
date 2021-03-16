package net.vicnix.friends.session;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.vicnix.friends.VicnixFriends;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Session {

    private final String name;

    private final UUID uuid;

    private List<String> friends = new ArrayList<>();

    private List<String> requests = new ArrayList<>();

    public Session(String name, UUID uuid) {
        this.name = name;

        this.uuid = uuid;
    }

    public Session(String name, UUID uuid, List<String> friends, List<String> requests) {
        this(name, uuid);

        this.friends = friends;

        this.requests = requests;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public List<String> getFriends() {
        return this.friends;
    }

    public void addFriend(Session session) {
        this.addFriend(session.getUniqueId());
    }

    public void addFriend(UUID uuid) {
        if (this.isFriend(uuid)) return;

        this.friends.add(uuid.toString());
    }

    public void removeFriend(Session session) {
        this.removeFriend(session.getUniqueId());
    }

    public void removeFriend(UUID uuid) {
        this.friends.remove(uuid.toString());
    }

    public Boolean isFriend(Session session) {
        return this.isFriend(session.getUniqueId());
    }

    public Boolean isFriend(UUID uuid) {
        return this.friends.contains(uuid.toString());
    }

    public List<String> getRequests() {
        return this.requests;
    }

    public void addRequest(Session session) {
        this.addRequest(session.getUniqueId());
    }

    public void addRequest(UUID uuid) {
        this.requests.add(uuid.toString());
    }

    public void removeRequest(Session session) {
        this.removeRequest(session.getUniqueId());
    }

    public void removeRequest(UUID uuid) {
        this.requests.remove(uuid.toString());
    }

    public Boolean alreadyRequested(Session session) {
        return this.alreadyRequested(session.getUniqueId());
    }

    public Boolean alreadyRequested(UUID uuid) {
        return this.requests.contains(uuid.toString());
    }

    public Boolean isConnected() {
        return this.getInstance() != null;
    }

    public void sendMessage(String message) {
        if (!this.isConnected()) return;

        if (message.isEmpty()) return;

        this.getInstance().sendMessage(new TextComponent(message));
    }

    public void sendMessage(BaseComponent... baseComponent) {
        if (!this.isConnected()) return;

        this.getInstance().sendMessage(baseComponent);
    }

    public ProxiedPlayer getInstance() {
        return ProxyServer.getInstance().getPlayer(this.name);
    }

    public void intentSave() {
        this.intentSave(false);
    }

    public void intentSave(Boolean force) {
        if (this.isConnected() && !force) return;

        VicnixFriends.getInstance().getProvider().saveSession(this);
    }

    @Override
    public String toString() {
        return "Session{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                ", friends=" + friends +
                ", requests=" + requests +
                '}';
    }
}