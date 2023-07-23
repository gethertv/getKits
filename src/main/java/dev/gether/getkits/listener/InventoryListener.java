package dev.gether.getkits.listener;

import dev.gether.getkits.GetKits;
import dev.gether.getkits.data.Kit;
import dev.gether.getkits.data.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

    private final GetKits plugin;

    public InventoryListener(GetKits plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClickInv(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();

        for (Kit kit : plugin.getKitManager().getKitsData().values()) {
            if(kit.getInventory().equals(event.getInventory()))
            {
                event.setCancelled(true);
                return;
            }
        }

        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        if(user!=null)
        {
            if(event.getInventory().equals(user.getInventory())) {
                event.setCancelled(true);
                for (Kit kit : plugin.getKitManager().getKitsData().values()) {
                    if(event.getSlot()==kit.getSlot())
                    {
                        if(event.getClick()== ClickType.RIGHT)
                        {
                            player.openInventory(kit.getInventory());
                            return;
                        }
                        plugin.getKitManager().pickUp(user, player, kit);
                        return;
                    }
                }
            }



        }

    }
}
