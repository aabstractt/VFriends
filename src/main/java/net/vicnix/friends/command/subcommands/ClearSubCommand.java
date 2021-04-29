package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.vicnix.friends.VicnixFriends;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.translation.Translation;

@FriendAnnotationCommand(
        name = "clear",
        syntax = "/amigos clear all",
        description = "Eliminar todos tus amigos o las solicitudes"
)
public class ClearSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        if (args[0].equalsIgnoreCase("all")) {
            if (session.getSessionStorage().getFriends().isEmpty()) {
                session.sendMessage(Translation.getInstance().translateString("FRIEND_LIST_EMPTY"));

                return;
            }

            ProxyServer.getInstance().getScheduler().runAsync(VicnixFriends.getInstance(), () -> {
                session.getSessionStorage().removeFriends();

                session.sendMessage(ChatColor.GREEN + "Todos tus amigos han sido eliminados!");
            });
        }
    }
}