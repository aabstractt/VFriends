package net.vicnix.friends.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.vicnix.friends.command.FriendAnnotationCommand;
import net.vicnix.friends.command.FriendSubCommand;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionException;
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
        try {
            Session target = SessionManager.getInstance().getOfflineSession(args[0]);

            if (target.getUniqueId().equals(session.getUniqueId())) {
                session.sendMessage(new ComponentBuilder("No puedes enviarte un mensaje a ti mismo.").color(ChatColor.RED).create()[0]);

                return;
            }

            session.friendMessage(target, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        } catch (SessionException e) {
            session.sendMessage(new ComponentBuilder(e.getMessage()).color(ChatColor.RED).create());
        }
    }
}