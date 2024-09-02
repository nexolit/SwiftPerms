package me.nexolit.ezperms.permissions;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class KnownPermissionLoader implements Runnable {

    final File folder = Bukkit.getPluginManager().getPlugin("EzPerms").getDataFolder();
    final File file = new File(folder, "known-permissions.list");
    final Plugin[] plugins;
    final Logger logger;
    public Set<String> permissions = new HashSet<>();

    public KnownPermissionLoader(Logger logger) {
        if(!folder.exists()) {
            folder.mkdirs();
        } else {
            try {
                if(file.exists()) {
                    ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
                    permissions = (Set<String>) input.readObject();
                    input.close();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        plugins = Bukkit.getPluginManager().getPlugins();
        this.logger = logger;
    }

    @Override
    public void run() {
        logger.info("Updating plugin permissions");
        permissions.clear();
        Set<Permission> bukkitPermissions = Bukkit.getPluginManager().getPermissions();
        for (Permission permission : bukkitPermissions) {
            permissions.add(permission.getName());
        }

        for (Plugin plugin : plugins) {
            //FEATURE: We could get Plugin name to sort permissions by plugin name
            for(Map.Entry<String, Map<String, Object>> command : plugin.getDescription().getCommands().entrySet()) {
                if(command.getValue().get("permission") != null) {
                    permissions.add((String)command.getValue().get("permission"));
                }
            }
            for(Permission permission : plugin.getDescription().getPermissions()) {
                permissions.add(permission.getName());
                for(Map.Entry<String, Boolean> subPermission : permission.getChildren().entrySet()) {
                    permissions.add(subPermission.getKey());
                }
            }
        }
        save();
        logger.info(permissions.toString());
        logger.info("Updated plugin permissions");
    }

    public void save() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(permissions);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
