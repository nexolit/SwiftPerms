package me.nexolit.ezperms.vault;

import me.nexolit.ezperms.groups.Group;
import me.nexolit.ezperms.Main;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.UUID;

public class VaultConnection extends Permission {

    @Override
    public String getName() {
        return "EzPerms";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        if(Main.loader.permissions.add(permission))
            Main.loader.save();

        UUID player_uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
        ArrayList<String> playerPermissions = Main.manager.playerBoundPermissions.get(player_uuid);
        if(playerPermissions != null) {
            if(playerPermissions.contains(permission) || checkWildCard(permission, playerPermissions)) {
                return true;
            }
        }
        return groupHas(world, getPrimaryGroup(world, player), permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        UUID player_uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
        Main.manager.addPlayerBoundPermission(player_uuid, permission);
        return true;
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        UUID player_uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
        Main.manager.removePermissionFromPlayer(player_uuid, permission);
        return true;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        for(Group permGroup : Main.manager.groupManager.groups) {
            if(permGroup.name.equals(group)) {
                if(permGroup.permissions.contains(permission) || checkWildCard(permission, permGroup.permissions)) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        Main.manager.setGroupPerm(group, permission, true);
        return true;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        Main.manager.setGroupPerm(group, permission, false);
        return true;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        for(Group permGroup : Main.manager.groupManager.groups) {
            if(permGroup.name.equals(group)) {
                UUID player_uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
                if(permGroup.players.contains(player_uuid)) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        UUID player_uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
        Main.manager.groupManager.addPlayerToGroup(player_uuid, group);
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        UUID player_uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
        Main.manager.removePlayerFromGroup(player_uuid, group);
        return true;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        UUID player_uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
        for(Group group : Main.manager.groupManager.groups) {
            if(group.players.contains(player_uuid)) {
                return new String[] { group.name };
            }
        }
        return new String[0];
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        UUID player_uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
        for(Group group : Main.manager.groupManager.groups) {
            if(group.players.contains(player_uuid)) {
                return group.name;
            }
        }
        return null;
    }

    @Override
    public String[] getGroups() {
        ArrayList<String> groups = new ArrayList<>();
        for(Group group : Main.manager.groupManager.groups) {
            groups.add(group.name);
        }
        return groups.toArray(new String[0]);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    public boolean checkWildCard(String permission, ArrayList<String> permissionList) {
        for(String groupPermission : permissionList) {
            if(groupPermission.endsWith("*")) {
                String parentPermission = groupPermission.substring(0, groupPermission.length() - 2);
                if(permission.startsWith(parentPermission)) {
                    return true;
                }
            }
        }
        return false;
    }
}
