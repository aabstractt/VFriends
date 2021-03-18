package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;

@FriendAnnotationCommand(
        name = "notifications",
        syntax = "/amigos notifications",
        description = "Activar/Desactivar los mensajes cuando tus amigos entran al servidor",
        requiresArgumentCompletion = false
)
public class NotificationsSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        session.toggleNotifications(!session.hasToggleNotifications());

        session.sendMessage(new ComponentBuilder(String.format("Te has %s las notificaciones de cuando un amigo se conecta!", session.hasToggleNotifications() ? "activado" : "desactivado")).color(ChatColor.GOLD).create());
    }
}