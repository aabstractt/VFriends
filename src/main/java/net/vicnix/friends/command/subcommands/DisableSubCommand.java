package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionStorage;

@FriendAnnotationCommand(
        name = "disable",
        syntax = "/amigos disable",
        description = "Deshabilitar las solicitudes de amistad",
        requiresArgumentCompletion = false
)
public class DisableSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        SessionStorage sessionStorage = session.getSessionStorage();

        sessionStorage.toggleRequests(!sessionStorage.hasToggleRequests());

        session.sendMessage(new ComponentBuilder(String.format("Te has %s las solicitudes de amistad!", sessionStorage.hasToggleRequests() ? "activado" : "desactivado")).color(ChatColor.GOLD).create());
    }
}