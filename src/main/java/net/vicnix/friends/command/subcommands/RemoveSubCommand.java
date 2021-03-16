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
        name = "remove",
        syntax = "/amigos remove <jugador>",
        description = "Eliminar a un jugador de tu lista de amigos"
)
public class RemoveSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        try {
            Session target = SessionManager.getInstance().getOfflineSession(args[0]);

            if (!session.isFriend(target)) {
                session.sendMessage(new ComponentBuilder("Este jugador no esta en tu lista de amigos").color(ChatColor.RED).create()[0]);

                return;
            }

            target.removeFriend(session);
            target.intentSave();

            session.removeFriend(target);

            session.sendMessage(Translation.getInstance().translateString("FRIEND_REMOVED", target.getName()));
            target.sendMessage(Translation.getInstance().translateString("FRIEND_REMOVED_YOU", target.getName()));
        } catch (SessionException e) {
            session.sendMessage(new ComponentBuilder(e.getMessage()).color(ChatColor.RED).create()[0]);
        }
    }
}
