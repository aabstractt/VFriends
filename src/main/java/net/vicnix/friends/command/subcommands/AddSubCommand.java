package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

            if (target.alreadyRequested(session.getUniqueId())) {
                session.sendMessage(new TextComponent(Translation.getInstance().translateString("ALREADY_SENT_FRIEND_REQUEST", target.getName())));

                return;
            }

            if (target.isFriend(session)) {
                session.sendMessage(new ComponentBuilder("Este jugador ya esta en tu lista de amigos").color(ChatColor.RED).create());

                return;
            }

            if (session.alreadyRequested(target)) {
                session.acceptFriendRequest(target);

                return;
            }

            target.addRequest(session);

            target.intentSave();

            target.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', String.format("&e¡&d%s &ete ha &aenviado &euna solicitud de amistad!", session.getName())))
                    .append(" - ").color(ChatColor.AQUA)
                    .append("ACEPTAR").color(ChatColor.GREEN).bold(true)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Aceptar la solicitud de amistad").color(ChatColor.GREEN).create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigos accept " + session.getName()))
                    .append(" RECHAZAR").color(ChatColor.RED).bold(true)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Rechazar solicitud de amistad").color(ChatColor.RED).create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigos deny " + session.getName()))
                    .create());

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