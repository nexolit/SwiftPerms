package me.nexolit.ezperms;

import me.nexolit.ezperms.groups.Group;
import me.nexolit.ezperms.groups.GroupManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.Callable;

public class Bstats {

    private static final String DOWNLOAD_SOURCE = "Github";
    private static boolean initialized = false;

    public static void init(Plugin plugin, boolean usesVault, GroupManager groupManager) {
        if(initialized) return;

        Metrics metrics = new Metrics(plugin, 23265);

        metrics.addCustomChart(new SimplePie("groups", new Callable<String>() {
            @Override
            public String call() throws Exception {
                int groupCount = groupManager.groups.size();
                int lastValue = 0;
                for(int i = 0; i < groupCount; i += 10) {
                    lastValue = i;
                }
                return lastValue + "-" + (lastValue + 10);
            }
        }));

        metrics.addCustomChart(new SimplePie("permissions", new Callable<String>() {
            @Override
            public String call() throws Exception {
                int count = 0;
                for(Group group : groupManager.groups) {
                    count += group.permissions.size();
                }
                int lastValue = 0;
                for(int i = 0; i < count; i += 10) {
                    lastValue = i;
                }
                return lastValue + "-" + (lastValue + 10);
            }
        }));

        metrics.addCustomChart(new SimplePie("vault", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return String.valueOf(usesVault);
            }
        }));

        metrics.addCustomChart(new SimplePie("source", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return DOWNLOAD_SOURCE;
            }
        }));

        initialized = true;
    }
}
