package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionException;
import net.vicnix.friends.session.SessionManager;
import net.vicnix.friends.translation.Translation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@FriendAnnotationCommand(
        name = "list",
        syntax = "/amigos list",
        description = "Ver tu lista de amigos",
        requiresArgumentCompletion = false
)
public class ListSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        List<String> friends = session.getFriends();

        session.sendMessage(Translation.getInstance().translateString("FRIENDS_LIST", String.valueOf(friends.size()), "10"));

        if (friends.isEmpty()) {
            session.sendMessage(Translation.getInstance().translateString("FRIEND_LIST_EMPTY"));

            return;
        }

        List<String> offline = new ArrayList<>();

        for (String uuid : friends) {
            try {
                Session target = SessionManager.getInstance().getOfflineSession(UUID.fromString(uuid));

                if (!target.isConnected()) {
                    offline.add(target.getName());

                    continue;
                }

                BaseComponent[] components = new ComponentBuilder(Translation.getInstance().translateString("FRIEND_ONLINE_HOVER", target.getName()))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(Translation.getInstance().translateString("FRIEND_ONLINE_HOVER_TEXT", target.getName()))}))
                        .append(Translation.getInstance().translateString("FRIEND_ONLINE", Translation.getInstance().translateServerPrefix(target.getInstance().getServer().getInfo())), ComponentBuilder.FormatRetention.NONE)
                        .create();

                session.sendMessage(components[0], components[1]);
            } catch (SessionException e) {
                session.sendMessage(new ComponentBuilder(e.getMessage()).color(ChatColor.RED).create()[0]);

                return;
            }
        }

        BaseComponent[] components = new ComponentBuilder(Translation.getInstance().translateString("FRIEND_OFFLINE_TEXT")).append(String.join(", ", offline.toArray(new String[0]))).color(ChatColor.RED).create();

        session.sendMessage(components[0], components[1]);
    }
}