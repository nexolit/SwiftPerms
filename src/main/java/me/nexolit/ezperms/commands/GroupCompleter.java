package me.nexolit.ezperms.commands;

import me.nexolit.ezperms.groups.Group;
import me.nexolit.ezperms.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            return List.of("add", "remove", "player");
        } else if(args.length == 2 && args[0].equalsIgnoreCase("add")) {
            return List.of("Enter Group Name");
        } else if(args[0].equalsIgnoreCase("remove") && args.length == 2
            || args.length == 3 && args[0].equalsIgnoreCase("player")) {
            List<String> groupNames = new ArrayList<>();
            for (Group group : Main.manager.groupManager.groups) {
                groupNames.add(group.name);
            }
            return groupNames;
        } else if(args.length == 2 && args[0].equalsIgnoreCase("player")) {
            return List.of("add", "remove");
        } else if(args.length == 4 && args[0].equalsIgnoreCase("player")) {
            if(args[1].equalsIgnoreCase("add")) {
                List<String> players = new ArrayList<>();
                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    players.add(player.getName());
                }
                return players;
            } else if(args[1].equalsIgnoreCase("remove")) {
                Group group = Main.manager.groupManager.getGroup(args[2]);
                if(group != null) {
                    ArrayList<String> playerNames = new ArrayList<>();
                    for(UUID player_uuid : group.players) {
                        String playerName = Bukkit.getOfflinePlayer(player_uuid).getName();
                        if(playerName != null) playerNames.add(playerName);
                    }
                    return playerNames;
                }
            }
        }
        return List.of();
    }
}
