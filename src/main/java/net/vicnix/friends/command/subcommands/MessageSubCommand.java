package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionManager;

import java.util.Arrays;

@FriendAnnotationCommand(
        name = "message",
        syntax = "/amigos message <jugador>",
        description = "Enviar un mensaje a alguien de tu lista de amigos"
)
public class MessageSubCommand extends FriendSubCommand {

    @Override
    public void execute(Session session, String[] args) {
        Session target = SessionManager.getInstance().getOfflineSession(args[0]);

        if (target == null) {
            session.sendMessage(new ComponentBuilder("Jugador no encontrado").color(ChatColor.RED).create());

            return;
        }

        if (target.getUniqueId().equals(session.getUniqueId())) {
            session.sendMessage(new ComponentBuilder("No puedes enviarte un mensaje a ti mismo.").color(ChatColor.RED).create()[0]);

            return;
        }

        session.friendMessage(target, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
    }
}