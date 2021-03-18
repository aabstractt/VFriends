package net.vicnix.friends.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionException;
import net.vicnix.friends.session.SessionManager;

import java.util.Arrays;

public class FriendMessageCommand extends Command {

    public FriendMessageCommand() {
        super("fmsg", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new ComponentBuilder("Uso: /fmsg <jugador> <mensaje>").color(ChatColor.RED).create());

            return;
        }

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Run this command in-game").color(ChatColor.RED).create());

            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        Session session = SessionManager.getInstance().getSessionPlayer(player);

        if (session == null) {
            player.sendMessage(new ComponentBuilder("An error occurred!").color(ChatColor.RED).create());

            return;
        }

        try {
            Session target = SessionManager.getInstance().getOfflineSession(args[0]);

            if (target.getUniqueId().equals(session.getUniqueId())) {
                session.sendMessage(new ComponentBuilder("No puedes enviarte un mensaje a ti mismo.").color(ChatColor.RED).create()[0]);

                return;
            }

            session.friendMessage(target, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        } catch (SessionException e) {
            e.printStackTrace();
        }
    }
}