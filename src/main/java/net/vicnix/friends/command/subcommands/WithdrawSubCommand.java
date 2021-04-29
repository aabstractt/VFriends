package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionManager;
import net.vicnix.friends.session.SessionStorage;

@FriendAnnotationCommand(
        name = "withdraw",
        syntax = "/amigos withdraw <juagdor>",
        description = "Cancelar una solicitud de amistad enviada"
)
public class WithdrawSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        Session target = SessionManager.getInstance().getOfflineSession(args[0]);

        if (target == null) {
            session.sendMessage(new ComponentBuilder("Jugador no encontrado").color(ChatColor.RED).create());

            return;
        }

        if (!target.getSessionStorage().alreadyRequested(session.getUniqueId())) {
            session.sendMessage(new ComponentBuilder("No le has enviado ninguna solicitud de amistad a este jugador").color(ChatColor.RED).create());

            return;
        }

        target.getSessionStorage().removeRequest(session.getUniqueId());
        SessionManager.getInstance().intentSave((SessionStorage) target.getSessionStorage().forceClone(), target.getMaxFriendsSlots());

        session.getSessionStorage().removeSentRequest(target.getUniqueId());

        session.sendMessage(new ComponentBuilder("Has cancelado la solicitud de amistad que le has enviado a " + target.getName()).color(ChatColor.GREEN).create());
    }
}