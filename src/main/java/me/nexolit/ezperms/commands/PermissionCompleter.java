package me.nexolit.ezperms.commands;

import me.nexolit.ezperms.groups.Group;
import me.nexolit.ezperms.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissionCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                return List.of("group", "player", "refresh");
            case 2:
                if(args[0].equalsIgnoreCase("group")) {
                    List<String> groupNames = new ArrayList<>();
                    for (Group group : Main.manager.groupManager.groups) {
                        groupNames.add(group.name);
                    }
                    return groupNames;
                } else if(args[0].equalsIgnoreCase("player")) {
                    List<String> players = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        players.add(player.getName());
                    }
                    return players;
                }
            case 3:
                if(args[0].equalsIgnoreCase("player")
                        || args[0].equalsIgnoreCase("group")) {
                    return List.of("add", "remove");
                }
            case 4:
                if(List.of("group", "player").contains(args[0])) {
                    if(args[2].equalsIgnoreCase("remove")) {
                        if(args[0].equalsIgnoreCase("group")) {
                            Group group = Main.manager.groupManager.getGroup(args[1]);
                            if(group != null) return group.permissions;
                        } else {
                            UUID player_uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
                            ArrayList<String> playerPermissions = Main.manager.playerBoundPermissions.get(player_uuid);
                            if(playerPermissions != null) return playerPermissions;
                        }
                    } else if(args[2].equalsIgnoreCase("add")) {
                        return new ArrayList<String>(Main.loader.permissions);
                    }
                }
                return List.of();
            default:
                return List.of();
        }
    }
}
