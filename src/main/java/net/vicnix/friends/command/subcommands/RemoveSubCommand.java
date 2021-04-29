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
        name = "remove",
        syntax = "/amigos remove <jugador>",
        description = "Eliminar a un jugador de tu lista de amigos"
)
public class RemoveSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        Session target = SessionManager.getInstance().getOfflineSession(args[0]);

        if (target == null) {
            session.sendMessage(new ComponentBuilder("Jugador no encontrado").color(ChatColor.RED).create());

            return;
        }

        if (!session.getSessionStorage().isFriend(target.getUniqueId())) {
            session.sendMessage(new ComponentBuilder("Este jugador no esta en tu lista de amigos").color(ChatColor.RED).create());

            return;
        }

        target.getSessionStorage().removeFriend(session.getUniqueId());
        SessionManager.getInstance().intentSave((SessionStorage) target.getSessionStorage().forceClone(), target.getMaxFriendsSlots());

        session.getSessionStorage().removeFriend(target.getUniqueId());

        session.sendMessage(Translation.getInstance().translateString("FRIEND_REMOVED", target.getName()));
        target.sendMessage(Translation.getInstance().translateString("FRIEND_REMOVED_YOU", session.getName()));
    }
}