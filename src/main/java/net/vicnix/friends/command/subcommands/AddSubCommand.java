package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionManager;
import net.vicnix.friends.session.SessionStorage;
import net.vicnix.friends.translation.Translation;

@FriendAnnotationCommand(
        name = "add",
        syntax = "/amigos add <jugador>",
        description = "Enviar solicitud de amigos a un jugador"
)
public class AddSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        Session target = SessionManager.getInstance().getOfflineSession(args[0]);

        if (target == null) {
            session.sendMessage(new ComponentBuilder("Jugador no encontrado").color(ChatColor.RED).create());

            return;
        }

        if (target.getUniqueId().equals(session.getUniqueId())) {
            session.sendMessage(new ComponentBuilder("No puedes enviarte una solicitud de amistad a ti mismo.").color(ChatColor.RED).create()[0]);

            return;
        }

        if (!target.getSessionStorage().hasToggleRequests()) {
            session.sendMessage(new ComponentBuilder("No puedes enviarle solicitudes de amistad a este usuario").color(ChatColor.RED).create());

            return;
        }

        if (session.getSessionStorage().getFriends().size() >= session.getMaxFriendsSlots()) {
            session.sendMessage(new ComponentBuilder("Tu lista de amigos esta totalmente llena, compra un rango mas superior en").color(ChatColor.RED)
                    .append("\n tienda.vincix.net ").color(ChatColor.GREEN)
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://tienda.vicnix.net"))
                    .append("para tener mas slots de amigos!").color(ChatColor.RED).create());

            return;
        }

        if (target.getSessionStorage().getFriends().size() >= target.getMaxFriendsSlots()) {
            session.sendMessage(new ComponentBuilder(target.getName() + " tiene la lista de amigos llena.").color(ChatColor.RED).create());

            return;
        }

        if (target.getSessionStorage().alreadyRequested(session.getUniqueId())) {
            session.sendMessage(new TextComponent(Translation.getInstance().translateString("ALREADY_SENT_FRIEND_REQUEST", target.getName())));

            return;
        }

        if (target.getSessionStorage().isFriend(session.getUniqueId())) {
            session.sendMessage(new ComponentBuilder("Este jugador ya esta en tu lista de amigos").color(ChatColor.RED).create());

            return;
        }

        if (session.getSessionStorage().alreadyRequested(target.getUniqueId())) {
            session.acceptFriendRequest(target);

            return;
        }

        target.getSessionStorage().addRequest(session.getUniqueId());

        SessionManager.getInstance().intentSave((SessionStorage) target.getSessionStorage().forceClone(), target.getMaxFriendsSlots());

        target.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', String.format("&e¡&d%s &ete ha &aenviado &euna solicitud de amistad!", session.getName())))
                .append(" - ").color(ChatColor.AQUA)
                .append("ACEPTAR").color(ChatColor.GREEN).bold(true)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Aceptar la solicitud de amistad").color(ChatColor.GREEN).create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigos accept " + session.getName()))
                .append(" | ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_GRAY).bold(true)
                .append("RECHAZAR").color(ChatColor.RED).bold(true)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Rechazar solicitud de amistad").color(ChatColor.RED).create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigos deny " + session.getName()))
                .create());

        session.getSessionStorage().addSentRequest(target.getUniqueId());

        session.sendMessage(Translation.getInstance().translateString("FRIEND_REQUEST_SENT", target.getName()));
    }
}