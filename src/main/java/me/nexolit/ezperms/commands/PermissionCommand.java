package me.nexolit.ezperms.commands;

import me.nexolit.ezperms.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class PermissionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandResult result = null;
        if(sender.hasPermission("ezperms.permissions") && args.length > 0) {
            if(args[0].equalsIgnoreCase("refresh")) {
                Main main = (Main) Bukkit.getPluginManager().getPlugin("EzPerms");
                if (main != null) {
                    main.refresh();
                    result = new CommandResult("Successfully refreshed permissions", CommandResult.SUCCESS);
                } else result = new CommandResult("Main instance not found", CommandResult.FAIL);
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("player")) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    UUID player_uuid = player.getUniqueId();
                    if (args[2].equalsIgnoreCase("add")) {
                        result = Main.manager.addPlayerBoundPermission(player_uuid, args[3]);
                        Main.manager.logger.info("Permission " + args[3] + " was added to player: " + Bukkit.getPlayer(player_uuid).getDisplayName());
                    } else if (args[2].equalsIgnoreCase("remove")) {
                        result = Main.manager.removePermissionFromPlayer(player_uuid, args[3]);
                        Main.manager.logger.info("Permission " + args[3] + " was removed from player: " + Bukkit.getPlayer(player_uuid).getDisplayName());
                    }
                    if(player.isOnline()) player.getPlayer().updateCommands();
                } else if(args[0].equalsIgnoreCase("group")) {
                    if (args[2].equalsIgnoreCase("add")) {
                        result = Main.manager.setGroupPerm(args[1], args[3], true);
                    } else if (args[2].equalsIgnoreCase("remove")) {
                        result = Main.manager.setGroupPerm(args[1], args[3], false);
                    }
                }
            }
        } else if (!sender.hasPermission("ezperms.permissions")) {
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
