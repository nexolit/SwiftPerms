package me.nexolit.ezperms;

import me.nexolit.ezperms.commands.GroupCommand;
import me.nexolit.ezperms.commands.GroupCompleter;
import me.nexolit.ezperms.commands.PermissionCommand;
import me.nexolit.ezperms.commands.PermissionCompleter;
import me.nexolit.ezperms.events.PlayerJoinListener;
import me.nexolit.ezperms.permissions.KnownPermissionLoader;
import me.nexolit.ezperms.permissions.PermissionManager;
import me.nexolit.ezperms.vault.VaultConnection;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    public static PermissionManager manager;
    public static KnownPermissionLoader loader;
    private final Logger logger = getLogger();
    public boolean usesVault;

    @Override
    public void onEnable() {
        PluginCommand permissionCommand = Bukkit.getPluginCommand("permission");
        PluginCommand groupCommand = Bukkit.getPluginCommand("group");

        permissionCommand.setExecutor(new PermissionCommand());
        permissionCommand.setTabCompleter(new PermissionCompleter());

        groupCommand.setExecutor(new GroupCommand());
        groupCommand.setTabCompleter(new GroupCompleter());

        loader = new KnownPermissionLoader(logger);
        manager = new PermissionManager(logger, getDataFolder());
        Bukkit.getScheduler().runTaskLater(this, loader, 200L);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);

        try {
            Class.forName("net.milkbowl.vault.permission.Permission");
            VaultConnection vaultConnection = new VaultConnection();
            Bukkit.getServicesManager().register(Permission.class, vaultConnection, this, ServicePriority.High);
            usesVault = true;
        } catch (ClassNotFoundException e) {
            usesVault = false;
            getLogger().warning("Vault not found! (This isn't an issue if no plugin is going to use it)\nError: " + e.getClass().getName());
        }

        Bstats.init(this, usesVault, manager.groupManager);
        getLogger().info("Loaded: " + this.getName());
    }

    public void refresh() {
        loader = new KnownPermissionLoader(logger);
        manager = new PermissionManager(logger, getDataFolder());
        loader.run();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Shutdown: " + this.getName());
    }

}
