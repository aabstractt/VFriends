package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.vicnix.friends.VicnixFriends;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionException;
import net.vicnix.friends.session.SessionManager;
import net.vicnix.friends.translation.Translation;

@FriendAnnotationCommand(
        name = "add",
        syntax = "/amigos add <jugador>",
        description = "Enviar solicitud de amigos a un jugador",
        requiresArgumentCompletion = true
)
public class AddSubCommand extends FriendSubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        try {
            Session session = SessionManager.getInstance().getSession(args[0]);

            if (session.getUuid().equals(player.getUniqueId())) {
                session.sendMessage(new ComponentBuilder("No puedes enviarte una solicitud de amistad a ti mismo.").color(ChatColor.RED).create()[0]);

                return;
            }

            if (session.alreadyRequested(player)) {
                player.sendMessage(new TextComponent(Translation.getInstance().translateString("ALREADY_SENT_FRIEND_REQUEST", session.getName())));

                return;
            }

            session.addRequest(player);

            if (!session.isConnected()) VicnixFriends.getInstance().getProvider().saveSession(session);

            session.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_RECEIVE", player.getName()));

            player.sendMessage(new TextComponent(Translation.getInstance().translateString("FRIEND_REQUEST_SENT", session.getName())));
        } catch (SessionException e) {
            player.sendMessage(new TextComponent(e.getMessage()));
        }
    }
}