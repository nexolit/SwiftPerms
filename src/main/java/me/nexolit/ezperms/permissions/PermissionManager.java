package me.nexolit.ezperms.permissions;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.nexolit.ezperms.commands.CommandResult;
import me.nexolit.ezperms.groups.Group;
import me.nexolit.ezperms.Main;
import me.nexolit.ezperms.groups.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class PermissionManager {

    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    final File folder;
    final File groupFile;
    final File playerFile;
    public GroupManager groupManager = new GroupManager();
    private final int DEFAULT_GROUP = 0;
    public HashMap<UUID, ArrayList<String>> playerBoundPermissions = new HashMap<>();
    public final Logger logger;

    public PermissionManager(Logger logger, File storageFolder) {
        folder = storageFolder;
        groupFile = new File(folder, "groups.json");
        playerFile = new File(folder, "players.json");

        if(!folder.exists()) {
            folder.mkdirs();
        } else {
            if(groupFile.exists()) {
                groupManager = gson.fromJson(loadFile(groupFile), GroupManager.class);
            } else {
                groupManager.groups.add(new Group("default"));
                saveGroupsToJson();
            }
            if(playerFile.exists()) {
                playerBoundPermissions = gson.fromJson(loadFile(playerFile),
                        new TypeToken<HashMap<UUID, ArrayList<String>>>(){}.getType());
            }
        }

        this.logger = logger;
    }

    private String loadFile(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void attachPerms(UUID player_uuid) {
        ArrayList<String> playerPermissions = playerBoundPermissions.get(player_uuid);
        if(playerPermissions != null) {
            for (String permission : playerPermissions) {
                setPlayerPerm(player_uuid,
                        Bukkit.getPluginManager().getPlugin("EzPerms"),
                        permission, true);
            }
        }

        boolean playerGroupFound = false;
        for (Group group : groupManager.groups) {
            if (group.players.contains(player_uuid)) {
                for (String permission : group.permissions) {
                    setPlayerPerm(player_uuid,
                            Bukkit.getPluginManager().getPlugin("EzPerms"),
                            permission, true);
                }
                playerGroupFound = true;
                break;
            }
        }

        if(!playerGroupFound
                && groupManager.groups.get(DEFAULT_GROUP).name.equals("default")) {
            groupManager.groups.get(DEFAULT_GROUP).players.add(player_uuid);
            attachGroupPerms(player_uuid);
            saveGroupsToJson();
        }

        //Bukkit.getPlayer(player_uuid).recalculatePermissions(); // WONT HELP!!!!
        Bukkit.getPlayer(player_uuid).updateCommands();
    }

    public CommandResult attachGroupPerms(UUID player_uuid) {
        if(Bukkit.getPlayer(player_uuid) != null) {
            for (Group group : groupManager.groups) {
                if (group.players.contains(player_uuid)) {
                    for (String permission : group.permissions) {
                        setPlayerPerm(player_uuid,
                                Bukkit.getPluginManager().getPlugin("EzPerms"),
                                permission, true);
                    }
                    return new CommandResult("Successfully attached permissions", CommandResult.SUCCESS);
                }
            }
            return new CommandResult("Couldn't find player", CommandResult.FAIL);
        }
        return new CommandResult("Player not online (probably)", CommandResult.SUCCESS);
    }

    public CommandResult addPlayerBoundPermission(UUID player_uuid, String permission) {
        ArrayList<String> playerPermissions = playerBoundPermissions.get(player_uuid);
        if (playerPermissions == null) {
            playerPermissions = new ArrayList<String>();
        }
        playerPermissions.add(permission);
        playerBoundPermissions.put(player_uuid, playerPermissions);

        if(Bukkit.getPlayer(player_uuid) != null) {
            setPlayerPerm(player_uuid,
                    Bukkit.getPluginManager().getPlugin("EzPerms"),
                    permission, true);
        }
        savePlayerPermissionsToJson();
        return new CommandResult("Successfully added permission to player", CommandResult.SUCCESS);
    }

    public CommandResult removePermissionFromPlayer(UUID player_uuid, String permission) {
        CommandResult result;
        ArrayList<String> playerPermissions = playerBoundPermissions.get(player_uuid);
        if(playerPermissions.remove(permission))
            result = new CommandResult("Successfully removed permission from player", CommandResult.SUCCESS);
        else
            result = new CommandResult("Couldn't find the permission", CommandResult.FAIL);

        if(!playerPermissions.isEmpty()) {
            playerBoundPermissions.put(player_uuid, playerPermissions);
        } else {
            playerBoundPermissions.remove(player_uuid);
        }

        if(Bukkit.getPlayer(player_uuid) != null) {
            setPlayerPerm(player_uuid,
                    Bukkit.getPluginManager().getPlugin("EzPerms"),
                    permission, false);
        }
        savePlayerPermissionsToJson();
        return result;
    }

    public CommandResult detachAllGroupPermsFromPlayer(UUID player_uuid) {
        for(Group group : groupManager.groups) {
            if(group.players.contains(player_uuid)) {
                if(Bukkit.getPlayer(player_uuid) != null) {
                    for (String permission : group.permissions) {
                        setPlayerPerm(player_uuid,
                                Bukkit.getPluginManager().getPlugin("EzPerms"),
                                permission, false);
                    }
                }
                return new CommandResult("Successfully detached permissions", CommandResult.SUCCESS);
            }
        }
        return new CommandResult("Couldn't find player", CommandResult.FAIL);
    }

    public CommandResult newGroup(Group group) {
        CommandResult result;
        result = groupManager.newGroup(group);
        saveGroupsToJson();
        return result;
    }

    public CommandResult removeGroup(String group_name) {
        for(Group group : groupManager.groups) {
            if(group.name.equalsIgnoreCase(group_name)) {
                Group removedGroup = groupManager.removeGroup(group_name);
                for (UUID player_uuid : removedGroup.players) {
                    if (Bukkit.getPlayer(player_uuid) != null) {
                        for (String permission : removedGroup.permissions) {
                            setPlayerPerm(player_uuid,
                                    Bukkit.getPluginManager().getPlugin("EzPerms"),
                                    permission, false);
                        }
                    }
                    if(groupManager.groups.get(DEFAULT_GROUP).name.equals("default"))
                        addPlayerToGroup(player_uuid, "default");
                }
                saveGroupsToJson();
                return new CommandResult("Successfully removed group " + group_name, CommandResult.SUCCESS);
            }
        }
        return new CommandResult(group_name + " - Group does not exist", CommandResult.FAIL);
    }

    public CommandResult addPlayerToGroup(UUID player_uuid, String group_name) {
        // Will remove his permissions if he's already in a group
        removePlayerFromGroup(player_uuid, groupManager.getPlayerGroup(player_uuid).name);
        //Ignore the result for now. The player can be without a group.
        CommandResult addPlayerToGroupResult = groupManager.addPlayerToGroup(player_uuid, group_name);
        if(addPlayerToGroupResult.getStatus() == CommandResult.FAIL) return addPlayerToGroupResult;
        CommandResult attachGroupPermsResult = attachGroupPerms(player_uuid);
        if(attachGroupPermsResult.getStatus() == CommandResult.FAIL) return attachGroupPermsResult;
        saveGroupsToJson();
        return addPlayerToGroupResult;
    }

    public CommandResult removePlayerFromGroup(UUID player_uuid, String group_name) {
        CommandResult result = detachAllGroupPermsFromPlayer(player_uuid);
        if(result.getStatus() == CommandResult.SUCCESS) {
            result = groupManager.removePlayerFromGroup(player_uuid, group_name);
            saveGroupsToJson();
        }
        return result;
    }

    public void saveGroupsToJson() {
        try {
            FileOutputStream outputStream = new FileOutputStream(groupFile);
            outputStream.write(gson.toJson(groupManager).getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlayerPermissionsToJson() {
        try {
            FileOutputStream outputStream = new FileOutputStream(playerFile);
            outputStream.write(gson.toJson(playerBoundPermissions).getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CommandResult setGroupPerm(String group_name, String permission, boolean allowed) {
        CommandResult result;

        for(Group group : groupManager.groups) {
            if(group.name.equalsIgnoreCase(group_name)) {
                for (UUID player_uuid : group.players) {
                    Player player = Bukkit.getPlayer(player_uuid);
                    if (player != null) {
                        setPlayerPerm(player_uuid,
                                Bukkit.getPluginManager().getPlugin("EzPerms"),
                                permission, allowed);
                        player.updateCommands();
                    }
                }

                if(allowed) {
                    groupManager.addPermission(group_name, permission);
                    logger.info("Permission " + permission + " was added to group: " + group_name);
                    result = new CommandResult("Successfully added permission to group " + group_name, CommandResult.SUCCESS);
                } else {
                    result = groupManager.removePermission(group_name, permission);
                    if(result.getStatus() == CommandResult.SUCCESS)
                        logger.info("Permission " + permission + " was removed from group: " + group_name);
                }

                saveGroupsToJson();
                return result;
            }
        }

        result = new CommandResult(group_name + " - Group does not exist", CommandResult.FAIL);
        return result;
    }

    public void setPlayerPerm(UUID player_uuid, Plugin plugin, String permission, boolean allowed) {
        Player player = Bukkit.getPlayer(player_uuid);
        PermissionAttachment attachment = player.addAttachment(plugin);
        if(!permission.endsWith("*") || Main.loader.permissions.contains(permission)) {
            attachment.setPermission(permission, allowed);
        } else {
            String parentPermission = permission.substring(0, permission.length() - 2);
            for(String knownPermission : Main.loader.permissions) {
                if(knownPermission.startsWith(parentPermission)) {
                    attachment.setPermission(knownPermission, allowed);
                }
            }
        }
    }
}
