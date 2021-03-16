package net.vicnix.friends.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.vicnix.friends.command.subcommands.AddSubCommand;

import java.util.*;

public class FriendsCommand extends Command implements TabExecutor {

    private final Map<String, FriendSubCommand> commands = new HashMap<>();
    private final Map<String, String> commandsAlias = new HashMap<>();

    public FriendsCommand() {
        super("friends", null, "f", "amigos");

        this.registerSubCommand(new AddSubCommand());
        this.registerSubCommand(new AddSubCommand(), "añadir");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("Run this command in-game"));

            return;
        }

        ProxiedPlayer player = ((ProxiedPlayer) sender);

        if (player.getServer().getInfo().getName().contains("Auth")) {
            player.sendMessage(new ComponentBuilder("No puedes ejecutar comandos en el auth").color(ChatColor.RED).create()[0]);

            return;
        }

        if (args.length <= 0) {
            showHelpMessage(player);

            return;
        }

        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);

        FriendSubCommand subCommand = this.getCommand(args[0]);

        if (subCommand == null) {
            showHelpMessage(player);

            return;
        }

        FriendAnnotationCommand annotations = subCommand.getAnnotations();

        if (annotations == null) return;

        if (newArgs.length == 0 && annotations.requiresArgumentCompletion()) {
            player.sendMessage(new TextComponent("Uso: "),
                    new ComponentBuilder(annotations.syntax()).color(ChatColor.GREEN)
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/amigos "
                                    + args[0] + " "))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatColor.YELLOW + "Click to prepare command.")}))
                            .create()[0]);
            return;
        }

        subCommand.execute(player, newArgs);
    }

    private void showHelpMessage(ProxiedPlayer player) {
        TextComponent topMSG = new TextComponent("Comandos de Amigos");

        topMSG.setColor(ChatColor.GOLD);
        topMSG.setBold(true);

        player.sendMessage(topMSG);

        TextComponent prepareMSG = new TextComponent("Click para ejecutar el comando.");

        prepareMSG.setColor(ChatColor.WHITE);
        prepareMSG.setItalic(true);

        for (FriendSubCommand command : commands.values()) {
            FriendAnnotationCommand annotations = command.getAnnotations();

            if (annotations == null) continue;

            TextComponent pt1 = new TextComponent(annotations.syntax());
            pt1.setColor(ChatColor.YELLOW);
            pt1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{prepareMSG}));
            pt1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/amigos " + annotations.name() + " "));

            TextComponent pt2 = new TextComponent(" - ");
            pt2.setColor(ChatColor.DARK_GRAY);

            TextComponent pt3 = new TextComponent(annotations.description());
            pt3.setColor(ChatColor.WHITE);
            pt3.setItalic(true);

            player.sendMessage(pt1, pt2, pt3);
        }

        player.sendMessage(new TextComponent(" "));
    }

    private void registerSubCommand(FriendSubCommand subCommand) {
        registerSubCommand(subCommand, null);
    }

    private void registerSubCommand(FriendSubCommand subCommand, String name) {
        FriendAnnotationCommand annotation = subCommand.getAnnotations();

        if (annotation == null) return;

        if (name == null) {
            commands.put(annotation.name(), subCommand);
        } else {
            commandsAlias.put(name, annotation.name());
        }
    }

    private FriendSubCommand getCommand(String name) {
        String alias = this.commandsAlias.get(name.toLowerCase());

        if (alias != null) name = alias;

        return commands.get(name.toLowerCase());
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        List<String> complete = new ArrayList<>();

        if (!(commandSender instanceof ProxiedPlayer)) {
            return complete;
        }

        if (args.length == 0) {
            return complete;
        }

        if (args.length == 1) {
            String name = args[0];

            int lastSpaceIndex = name.lastIndexOf(' ');

            if (lastSpaceIndex >= 0) {
                name = name.substring(lastSpaceIndex + 1);
            }

            List<String> commands = new ArrayList<>(this.commands.keySet());

            commands.addAll(this.commandsAlias.keySet());

            for (String commandName : commands) {
                if (!commandName.toLowerCase().startsWith(name)) {
                    continue;
                }

                if (complete.contains(commandName)) continue;

                complete.add(commandName);
            }

            Collections.sort(complete);

            return complete;
        }

        FriendSubCommand command = this.getCommand(args[0]);

        if (command == null) return complete;

        return command.getComplete((ProxiedPlayer) commandSender, Arrays.copyOfRange(args, 1, args.length));
    }
}