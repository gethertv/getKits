package dev.gether.getkits.listener;

import dev.gether.getkits.GetKits;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ConnectionListener implements Listener {

    private final GetKits plugin;
    public ConnectionListener(GetKits plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getSqLite().loadUser(player);
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getSqLite().updateUser(player);
                plugin.getUserManager().getUserData().remove(player.getUniqueId());
                plugin.getUserManager().getAdminEditKit().remove(player.getUniqueId());
            }
        }.runTaskAsynchronously(plugin);
    }
}
