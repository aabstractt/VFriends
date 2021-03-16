package net.vicnix.friends.session;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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

    public UUID getUuid() {
        return this.uuid;
    }

    public List<String> getFriends() {
        return this.friends;
    }

    public List<String> getRequests() {
        return this.requests;
    }

    public void addRequest(ProxiedPlayer player) {
        this.requests.add(player.getUniqueId().toString());
    }

    public Boolean alreadyRequested(ProxiedPlayer player) {
        return this.requests.contains(player.getUniqueId().toString());
    }

    public Boolean isConnected() {
        return this.getInstance() != null;
    }

    public void sendMessage(String message) {
        if (!this.isConnected()) return;

        this.getInstance().sendMessage(new TextComponent(message));
    }

    public void sendMessage(BaseComponent... baseComponent) {
        if (!this.isConnected()) return;

        this.getInstance().sendMessage(baseComponent);
    }

    public ProxiedPlayer getInstance() {
        return ProxyServer.getInstance().getPlayer(this.name);
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