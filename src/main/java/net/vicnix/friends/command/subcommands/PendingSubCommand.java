package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
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
        name = "pending",
        syntax = "/amigos pending <requests/sent>",
        description = "Ver las solicitudes pendientes"
)
public class PendingSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        if (args[0].equalsIgnoreCase("requests")) {
            List<String> requests = session.getRequests();

            session.sendMessage(Translation.getInstance().translateString("FRIENDS_PENDING_LIST", String.valueOf(requests.size())));

            if (requests.isEmpty()) {
                session.sendMessage(new ComponentBuilder("No tienes ninguna solicitud de amistad pendiente").color(ChatColor.RED).create());

                return;
            }

            ProxyServer.getInstance().getScheduler().runAsync(VicnixFriends.getInstance(), () -> {
                for (String uuid : requests) {
                    try {
                        Session target = SessionManager.getInstance().getOfflineSession(UUID.fromString(uuid));

                        session.sendMessage(new ComponentBuilder(target.getName()).color(ChatColor.YELLOW)
                        .append(" - ").color(ChatColor.GRAY)
                        .append("ACEPTAR").color(ChatColor.DARK_GREEN).bold(true)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigos accept " + target.getName()))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click para").color(ChatColor.GREEN).append(" ACEPTAR ").color(ChatColor.DARK_GREEN).bold(true).append("esta solicitud de amistad").color(ChatColor.GREEN).bold(false).create()))
                        .append(" | ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GRAY).bold(true)
                        .append("RECHAZAR").color(ChatColor.DARK_RED).bold(true)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigos deny " + target.getName()))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click para").color(ChatColor.GREEN).append(" RECHAZAR ").color(ChatColor.DARK_RED).bold(true).append("esta solicitud de amistad").color(ChatColor.GREEN).bold(false).create()))
                        .create());
                    } catch (SessionException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (args[0].equalsIgnoreCase("sent")) {
            List<String> sentRequests = session.getSentRequests();

            session.sendMessage(Translation.getInstance().translateString("FRIENDS_PENDING_SENT_LIST", String.valueOf(sentRequests.size())));

            if (sentRequests.isEmpty()) {
                session.sendMessage(new ComponentBuilder("No has enviado ninguna solicitud de amigos").color(ChatColor.RED).create());

                return;
            }

            ProxyServer.getInstance().getScheduler().runAsync(VicnixFriends.getInstance(), () -> {
                for (String uuid : sentRequests) {
                    try {
                        Session target = SessionManager.getInstance().getOfflineSession(UUID.fromString(uuid));

                        session.sendMessage(new ComponentBuilder(target.getName()).color(ChatColor.YELLOW)
                                .append(" - ").color(ChatColor.GRAY)
                                .append("RETRACTAR").color(ChatColor.DARK_RED).bold(true)
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigos withdraw " + target.getName()))
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click para").color(ChatColor.GREEN).append(" RETRACTAR ").color(ChatColor.DARK_RED).bold(true).append("esta solicitud de amistad").color(ChatColor.GREEN).bold(false).create()))
                                .create());
                    } catch (SessionException e) {
                        e.printStackTrace();
                    }
                }
            });
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

        for (String subCommand : new String[]{"requests", "sent"}) {
            if (!subCommand.toLowerCase().startsWith(name)) {
                continue;
            }

            if (complete.contains(subCommand)) continue;

            complete.add(subCommand);
        }

        Collections.sort(complete);

        return complete;
    }
}