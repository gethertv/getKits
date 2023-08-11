package dev.gether.getkits.task;

import dev.gether.getkits.GetKits;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSave extends BukkitRunnable {
    private final GetKits plugin;

    public AutoSave(GetKits plugin)
    {
        this.plugin = plugin;
    }
    @Override
    public void run() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    plugin.getSqLite().updateUser(player);
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
