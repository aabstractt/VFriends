package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionException;
import net.vicnix.friends.session.SessionManager;
import net.vicnix.friends.translation.Translation;

@FriendAnnotationCommand(
        name = "deny",
        syntax = "/amigos deny <jugador>",
        description = "Rechazar una solicitud de amistad"
)
public class DenySubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        try {
            Session target = SessionManager.getInstance().getOfflineSession(args[0]);

            if (target.getUniqueId().equals(session.getUniqueId())) {
                session.sendMessage(new ComponentBuilder("No puedes denegar esta solicitud.").color(ChatColor.RED).create()[0]);

                return;
            }

            if (!session.alreadyRequested(target)) {
                session.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_NOT_FOUND"));

                return;
            }

            session.removeRequest(target);

            target.removeSentRequest(session);

            session.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_DENIED", target.getName()));

            target.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_AS_FRIEND_DENIED", session.getName()));

            target.intentSave();
        } catch (SessionException e) {
            session.sendMessage(new ComponentBuilder(e.getMessage()).color(ChatColor.RED).create());
        }
    }
}