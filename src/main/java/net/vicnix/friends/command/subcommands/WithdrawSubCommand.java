package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionException;
import net.vicnix.friends.session.SessionManager;

@FriendAnnotationCommand(
        name = "withdraw",
        syntax = "/amigos withdraw <juagdor>",
        description = "Cancelar una solicitud de amistad enviada"
)
public class WithdrawSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        try {
            Session target = SessionManager.getInstance().getOfflineSession(args[0]);

            if (!target.alreadyRequested(session)) {
                session.sendMessage(new ComponentBuilder("No le haz enviado ninguna solicitud de amistad a este jugador").color(ChatColor.RED).create());

                return;
            }

            target.removeRequest(session);
            target.intentSave();

            session.removeSentRequest(target);

            session.sendMessage(new ComponentBuilder("Haz cancelado la solicitud de amistad que le has enviado a " + target.getName()).color(ChatColor.GREEN).create());
        } catch (SessionException e) {
            e.printStackTrace();
        }
    }
}