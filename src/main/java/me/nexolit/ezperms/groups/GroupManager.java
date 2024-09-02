package me.nexolit.ezperms.groups;

import me.nexolit.ezperms.commands.CommandResult;

import java.util.ArrayList;
import java.util.UUID;

public class GroupManager {
    public ArrayList<Group> groups = new ArrayList<Group>();

    public CommandResult newGroup(Group new_group) {
        for(Group group : groups) {
            if(group.name.equals(new_group.name)) {
                return new CommandResult("Group with the same name already exists", CommandResult.FAIL);
            }
        }
        groups.add(new_group);
        return new CommandResult("Successfully added a new group " + new_group.name, CommandResult.SUCCESS);
    }
    public Group removeGroup(String name) {
        for(int i = 0; i < groups.size(); i++) {
            if(groups.get(i).name.equalsIgnoreCase(name)) {
                return groups.remove(i);
            }
        }
        return null;
    }

    public Group getGroup(String name) {
        for (Group group : groups) {
            if (group.name.equalsIgnoreCase(name)) {
                return group;
            }
        }
        return null;
    }

    public CommandResult addPlayerToGroup(UUID player, String group_name) {
        for(Group group : groups) {
            if(group.name.equalsIgnoreCase(group_name)) {
                group.players.add(player);
                return new CommandResult("Successfully added player to group " + group_name, CommandResult.SUCCESS);
            }
        }
        return new CommandResult("Couldn't find group " + group_name, CommandResult.FAIL);
    }

    public Group getPlayerGroup(UUID player_uuid) {
        for(Group group : groups) {
            if(group.players.contains(player_uuid)) {
                return group;
            }
        }
        return groups.get(0);
    }

    public CommandResult removePlayerFromGroup(UUID player, String group_name) {
        for(Group group : groups) {
            if(group.name.equalsIgnoreCase(group_name)) {
                for(UUID playerUuid : group.players) {
                    if(playerUuid.equals(player)) {
                        group.players.remove(playerUuid);
                        return new CommandResult("Successfully removed player from group " + group_name, CommandResult.SUCCESS);
                    }
                }
                return new CommandResult("Player wasn't found in group " + group_name, CommandResult.FAIL);
            }
        }
        return new CommandResult("Couldn't find group " + group_name, CommandResult.FAIL);
    }

    public void addPermission(String group_name, String permission) {
        for(Group group : groups) {
            if(group.name.equalsIgnoreCase(group_name)) {
                group.permissions.add(permission);
                break;
            }
        }
    }

    public CommandResult removePermission(String group_name, String permission) {
        CommandResult result;
        for(Group group : groups) {
            if(group.name.equalsIgnoreCase(group_name)) {
                if(group.permissions.remove(permission) == CommandResult.FAIL) {
                    result = new CommandResult(group_name + " - Group doesn't have the permission " + permission, CommandResult.FAIL);
                } else {
                    result = new CommandResult("Successfully removed permission from group " + group_name, CommandResult.SUCCESS);
                }
                return result;
            }
        }
        result = new CommandResult(group_name + " - Group does not exist", CommandResult.FAIL);
        return result;
    }
}
