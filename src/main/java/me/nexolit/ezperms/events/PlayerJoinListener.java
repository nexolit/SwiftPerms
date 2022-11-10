package me.nexolit.ezperms.events;

import me.nexolit.ezperms.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID PlayerUUID = event.getPlayer().getUniqueId();
        Main.manager.attachPerms(PlayerUUID);
    }

}
