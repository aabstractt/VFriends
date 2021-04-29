package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionManager;
import net.vicnix.friends.translation.Translation;

@FriendAnnotationCommand(
        name = "accept",
        description = "Aceptar una peticion de amistad",
        syntax = "/amigos accept <jugador>"
)
public class AcceptSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        Session target = SessionManager.getInstance().getOfflineSession(args[0]);

        if (target == null) {
            session.sendMessage(new ComponentBuilder("Jugador no encontrado").color(ChatColor.RED).create());

            return;
        }

        if (target.getUniqueId().equals(session.getUniqueId()) || !target.getSessionStorage().hasToggleRequests()) {
            session.sendMessage(new ComponentBuilder("No puedes aceptar esta solicitud.").color(ChatColor.RED).create()[0]);

            return;
        }

        if (session.getSessionStorage().getFriends().size() >= session.getMaxFriendsSlots()) {
            session.sendMessage(new ComponentBuilder("Tu lista de amigos esta totalmente llena, compra un rango mas superior en").color(ChatColor.RED)
                    .append("\n tienda.vincix.net ").color(ChatColor.GREEN)
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://tienda.vicnix.net"))
                    .append("para tener mas slots de amigos!").color(ChatColor.RED)
                    .create()
            );

            return;
        }

        if (target.getSessionStorage().getFriends().size() >= target.getMaxFriendsSlots()) {
            session.sendMessage(new ComponentBuilder(target.getName() + " tiene la lista de amigos llena.").color(ChatColor.RED).create());

            return;
        }

        if (!session.getSessionStorage().alreadyRequested(target.getUniqueId())) {
            session.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_NOT_FOUND"));

            return;
        }

        session.acceptFriendRequest(target);
    }
}