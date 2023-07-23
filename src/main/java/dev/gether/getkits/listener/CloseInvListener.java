package dev.gether.getkits.listener;

import dev.gether.getkits.GetKits;
import dev.gether.getkits.data.AdminUser;
import dev.gether.getkits.file.KitsFile;
import dev.gether.getkits.utils.ColorFixer;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CloseInvListener implements Listener {

    private final GetKits plugin;

    public CloseInvListener(GetKits plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCloseInv(InventoryCloseEvent event)
    {
        Player player = (Player) event.getPlayer();
        AdminUser adminUser = plugin.getUserManager().getAdminEditKit().get(player.getUniqueId());
        if(adminUser==null)
            return;

        FileConfiguration config = KitsFile.getConfig();
        Inventory inv = adminUser.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if(item==null || item.getType()== Material.AIR)
            {
                if(config.isSet("kits."+adminUser.getKey()+".items."+i))
                    config.set("kits."+adminUser.getKey()+".items."+i, null);

                continue;
            }

            config.set("kits."+adminUser.getKey()+".items."+i, item);
        }
        KitsFile.save();
        player.sendMessage(ColorFixer.addColors("&aPomyslnie zapisano zestaw &2"+adminUser.getKey()+"&a!"));
        plugin.reloadPlugin();
    }
}
