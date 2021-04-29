package net.vicnix.friends.session;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.vicnix.friends.VicnixFriends;
import net.vicnix.friends.translation.Translation;

import java.util.UUID;

public class Session {

    private UUID lastReplied = null;

    private final SessionStorage sessionStorage;

    public Session(SessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
    }

    public String getName() {
        return this.sessionStorage.getName();
    }

    public UUID getUniqueId() {
        return this.sessionStorage.getUniqueId();
    }

    public Integer getMaxFriendsSlots() {
        if (this.isConnected()) {
            return VicnixFriends.getInstance().getMaxFriendsSlots(this);
        }

        return this.sessionStorage.maxFriendsSlots;
    }

    public SessionStorage getSessionStorage() {
        return this.sessionStorage;
    }

    public UUID getLastReplied() {
        return this.lastReplied;
    }

    public Boolean isConnected() {
        return this.getInstance() != null;
    }

    public void sendMessage(String message) {
        if (!this.isConnected()) return;

        if (message.isEmpty()) return;

        this.sendMessage(new TextComponent(message));
    }

    public void sendMessage(BaseComponent... baseComponent) {
        if (!this.isConnected()) return;

        this.getInstance().sendMessage(baseComponent);
    }

    public ProxiedPlayer getInstance() {
        return ProxyServer.getInstance().getPlayer(this.getUniqueId());
    }


    public void acceptFriendRequest(Session target) {
        this.sessionStorage.removeRequest(target.getUniqueId());
        this.sessionStorage.addFriend(target.getUniqueId());

        target.getSessionStorage().removeSentRequest(this.getUniqueId());
        target.getSessionStorage().addFriend(this.getUniqueId());

        this.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_ACCEPTED", target.getName()));

        target.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_AS_FRIEND_ACCEPTED", this.getName()));

        SessionManager.getInstance().intentSave((SessionStorage) target.getSessionStorage().forceClone(), target.getMaxFriendsSlots());
    }

    public void friendMessage(Session target, String message) {
        if (!target.getSessionStorage().isFriend(this.getUniqueId())) {
            this.sendMessage(new ComponentBuilder("Este jugador no esta en tu lista de amigos").color(ChatColor.RED).create());

            return;
        }

        if (!target.isConnected()) {
            this.sendMessage(new ComponentBuilder("Este jugador no esta conectado").color(ChatColor.RED).create());

            return;
        }

        this.lastReplied = target.getUniqueId();
        target.lastReplied = this.getUniqueId();

        target.sendMessage(new ComponentBuilder("[Amigos] ").color(ChatColor.YELLOW)
                .append(this.getName()).color(ChatColor.GRAY)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/amigos msg " + this.getName()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click para enviar un mensaje a " + target.getName()).color(ChatColor.GREEN).create()))
                .append(" -> ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.LIGHT_PURPLE)
                .append("Yo: ").color(ChatColor.GRAY)
                .append(message).color(ChatColor.WHITE)
                .create()
        );

        this.sendMessage(new ComponentBuilder("[Amigos] ").color(ChatColor.YELLOW)
                .append("Yo").color(ChatColor.GRAY)
                .append(" -> ").color(ChatColor.LIGHT_PURPLE)
                .append(target.getName() + ": ").color(ChatColor.GRAY)
                .append(message).color(ChatColor.WHITE)
                .create()
        );
    }
}