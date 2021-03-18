package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionException;
import net.vicnix.friends.session.SessionManager;
import net.vicnix.friends.translation.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FriendAnnotationCommand(
        name = "add",
        syntax = "/amigos add <jugador>",
        description = "Enviar solicitud de amigos a un jugador"
)
public class AddSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        try {
            Session target = SessionManager.getInstance().getOfflineSession(args[0]);

            if (target.getUniqueId().equals(session.getUniqueId())) {
                session.sendMessage(new ComponentBuilder("No puedes enviarte una solicitud de amistad a ti mismo.").color(ChatColor.RED).create()[0]);

                return;
            }

            if (target.alreadyRequested(session.getUniqueId())) {
                session.sendMessage(new TextComponent(Translation.getInstance().translateString("ALREADY_SENT_FRIEND_REQUEST", session.getName())));

                return;
            }

            if (target.isFriend(session)) {
                session.sendMessage(new ComponentBuilder("Este jugador ya esta en tu lista de amigos").color(ChatColor.RED).create());

                return;
            }

            target.addRequest(session);

            target.intentSave();

            target.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_RECEIVE", session.getName()));

            session.addSentRequest(target);

            session.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_SENT", target.getName()));
        } catch (SessionException e) {
            session.sendMessage(new ComponentBuilder(e.getMessage()).color(ChatColor.RED).create());
        }
    }

    @Override
    public List<String> getComplete(ProxiedPlayer player, String[] args) {
        List<String> complete = new ArrayList<>();

        String name = args[0];

        int lastSpaceIndex = name.lastIndexOf(' ');

        if (lastSpaceIndex >= 0) {
            name = name.substring(lastSpaceIndex + 1);
        }

        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (!proxiedPlayer.getName().toLowerCase().startsWith(name.toLowerCase())) {
                continue;
            }

            if (complete.contains(proxiedPlayer.getName())) continue;

            complete.add(proxiedPlayer.getName());
        }

        Collections.sort(complete);

        return complete;
    }
}