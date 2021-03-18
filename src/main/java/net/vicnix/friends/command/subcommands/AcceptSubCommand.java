package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.vicnix.friends.VicnixFriends;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionException;
import net.vicnix.friends.session.SessionManager;
import net.vicnix.friends.translation.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@FriendAnnotationCommand(
        name = "accept",
        description = "Aceptar una peticion de amistad",
        syntax = "/amigos accept <jugador>"
)
public class AcceptSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        try {
            Session target = SessionManager.getInstance().getOfflineSession(args[0]);

            if (target.getUniqueId().equals(session.getUniqueId())) {
                session.sendMessage(new ComponentBuilder("No puedes aceptar esta solicitud.").color(ChatColor.RED).create()[0]);

                return;
            }

            if (session.getFriends().size() >= VicnixFriends.getInstance().getMaxFriendsSlots(session)) {
                session.sendMessage(new ComponentBuilder("Tu lista de amigos esta totalmente llena, compra un rango mas superior en").color(ChatColor.RED)
                        .append("\n tienda.vincix.net ").color(ChatColor.GREEN)
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://tienda.vicnix.net"))
                        .append("para tener mas slots de amigos!").color(ChatColor.RED).create());

                return;
            }

            if (target.getFriends().size() >= VicnixFriends.getInstance().getMaxFriendsSlots(target)) {
                session.sendMessage(new ComponentBuilder(target.getName() + " tiene la lista de amigos llena.").color(ChatColor.RED).create());

                return;
            }

            if (!session.alreadyRequested(target)) {
                session.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_NOT_FOUND"));

                return;
            }

            session.acceptFriendRequest(target);
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

        Session session = SessionManager.getInstance().getSessionPlayer(player);

        if (session == null) {
            return complete;
        }

        for (String uuid : session.getRequests()) {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));

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