package me.nexolit.ezperms.commands;

import me.nexolit.ezperms.groups.Group;
import me.nexolit.ezperms.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GroupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandResult result = null;
        if(sender.hasPermission("ezperms.group") && args.length >= 2) {
            switch (args[0]) {
                case "add":
                    result = Main.manager.newGroup(new Group(args[1]));
                    break;
                case "remove":
                    result = Main.manager.removeGroup(args[1]);
                    break;
                case "player":
                    if(args[1].equalsIgnoreCase("add") && args.length == 4) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[3]);
                        result = Main.manager.addPlayerToGroup(player.getUniqueId(), args[2]);
                        if(player.isOnline()) player.getPlayer().updateCommands();
                    } else if(args[1].equalsIgnoreCase("remove") && args.length == 4) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[3]);
                        result = Main.manager.removePlayerFromGroup(player.getUniqueId(), args[2]);
                        if(player.isOnline()) player.getPlayer().updateCommands();
                    }
                    break;
            }
        } else if(!sender.hasPermission("ezperms.group")) {
            sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            return true;
        }

        if(result != null) {
            sender.sendMessage((result.getStatus() == CommandResult.SUCCESS
                    ? ChatColor.GREEN : ChatColor.RED) + result.getMessage());
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid command");
            return false;
        }
    }
}
