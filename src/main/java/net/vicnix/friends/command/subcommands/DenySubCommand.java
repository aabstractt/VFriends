package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionManager;
import net.vicnix.friends.session.SessionStorage;
import net.vicnix.friends.translation.Translation;

@FriendAnnotationCommand(
        name = "deny",
        syntax = "/amigos deny <jugador>",
        description = "Rechazar una solicitud de amistad"
)
public class DenySubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        Session target = SessionManager.getInstance().getOfflineSession(args[0]);

        if (target == null) {
            session.sendMessage(new ComponentBuilder("Jugador no encontrado").color(ChatColor.RED).create());

            return;
        }

        if (target.getUniqueId().equals(session.getUniqueId())) {
            session.sendMessage(new ComponentBuilder("No puedes denegar esta solicitud.").color(ChatColor.RED).create()[0]);

            return;
        }

        if (!session.getSessionStorage().alreadyRequested(target.getUniqueId())) {
            session.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_NOT_FOUND"));

            return;
        }

        session.getSessionStorage().removeRequest(target.getUniqueId());

        target.getSessionStorage().removeSentRequest(session.getUniqueId());

        session.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_DENIED", target.getName()));

        target.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_AS_FRIEND_DENIED", session.getName()));

        SessionManager.getInstance().intentSave((SessionStorage) target.getSessionStorage().forceClone(), target.getMaxFriendsSlots());
    }
}