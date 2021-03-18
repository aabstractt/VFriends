package net.vicnix.friends.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionManager;

import java.util.UUID;

public class FriendReplyCommand extends Command {

    public FriendReplyCommand() {
        super("fr");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Run this command in-game").color(ChatColor.RED).create());

            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length < 1) {
            player.sendMessage(new ComponentBuilder("Uso: /fr <mensaje>").color(ChatColor.RED).create());

            return;
        }

        Session session = SessionManager.getInstance().getSessionPlayer(player);

        if (session == null) {
            player.sendMessage(new ComponentBuilder("An error occurred!").color(ChatColor.RED).create());

            return;
        }

        if (session.getLastReplied() == null) {
            session.sendMessage(new ComponentBuilder("Jugador no encontrado").color(ChatColor.RED).create());

            return;
        }

        Session target = SessionManager.getInstance().getSessionUuid(UUID.fromString(session.getLastReplied()));

        if (target == null) {
            session.sendMessage(new ComponentBuilder("Jugador no encontrado").color(ChatColor.RED).create());

            return;
        }

        if (target.getUniqueId().equals(session.getUniqueId())) {
            session.sendMessage(new ComponentBuilder("No puedes enviarte un mensaje a ti mismo.").color(ChatColor.RED).create()[0]);

            return;
        }

        session.friendMessage(target, String.join(" ", args));
    }
}
