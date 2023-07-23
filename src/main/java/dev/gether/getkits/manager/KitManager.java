package dev.gether.getkits.manager;

import dev.gether.getkits.GetKits;
import dev.gether.getkits.data.ItemDrop;
import dev.gether.getkits.data.ItemGUI;
import dev.gether.getkits.data.Kit;
import dev.gether.getkits.data.User;
import dev.gether.getkits.file.KitsFile;
import dev.gether.getkits.utils.ColorFixer;
import dev.gether.getkits.utils.ItemUtils;
import dev.gether.getkits.utils.Timer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KitManager {


    private final GetKits plugin;
    private HashMap<String, Kit> kitsData = new HashMap<>();

    public KitManager(GetKits plugin)
    {
        this.plugin = plugin;
        KitsFile.loadFile();
        implementsKit();
    }

    private void implementsKit() {
        FileConfiguration config = KitsFile.getConfig();
        for(String kit : config.getConfigurationSection("kits").getKeys(false))
        {
            ConfigurationSection kitSection = config.getConfigurationSection("kits."+kit);

            String name = kitSection.getString(".name");
            String permission = kitSection.getString(".permission");
            int cooldown = kitSection.getInt(".cooldown");

            ConfigurationSection itemSection = kitSection.getConfigurationSection(".item");
            int slotKit = itemSection.getInt(".slot");
            ItemStack availableItem = ItemUtils.getItemFromConfig(itemSection.getConfigurationSection(".available"));
            ItemStack noAvailableItem = ItemUtils.getItemFromConfig(itemSection.getConfigurationSection(".no-available"));
            ItemStack cooldownItem = ItemUtils.getItemFromConfig(itemSection.getConfigurationSection(".cooldown"));

            /*
                Inventory preview drop
             */
            Inventory inventory = Bukkit.createInventory(
                    null,
                    kitSection.getInt(".inv.size"),
                    ColorFixer.addColors(kitSection.getString(".inv.title"))
            );

            ItemUtils.fillBackground(inventory, kitSection.getConfigurationSection(".inv.backgrounds"));

            List<ItemDrop> kitItems = new ArrayList<>();
            for(String slotStr : kitSection.getConfigurationSection(".items").getKeys(false))
            {
                int slot = Integer.parseInt(slotStr);
                ItemStack itemStack = kitSection.getItemStack(".items."+slotStr);

                inventory.setItem(slot, itemStack);
                kitItems.add(new ItemDrop(slot, itemStack));
            }

            ItemGUI itemGUI = new ItemGUI(slotKit, availableItem, noAvailableItem, cooldownItem);
            Kit kitObject = new Kit(kit, name, permission, cooldown, slotKit, kitItems, inventory, itemGUI);
            kitsData.put(kit, kitObject);

        }
    }

    public void pickUp(User user, Player player, Kit kit) {
        if(!player.hasPermission(kit.getPermission()))
        {
            player.sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.no-permission-to-kit")));
            player.closeInventory();
            return;
        }
        if(user.hasCooldown(kit))
        {
            int cooldown = (int) ((user.getCooldown().get(kit.getKeyName())+kit.getCooldown()*1000L)-System.currentTimeMillis())/1000;
            player.sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.cooldown").replace("{time}", Timer.getTime(cooldown))));
            player.closeInventory();
            return;
        }
        user.getCooldown().put(kit.getKeyName(), System.currentTimeMillis());
        player.closeInventory();
        player.sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.successfully-pick-up").replace("{name}", kit.getName())));

        kit.getKitItems().forEach(item -> player.getInventory().addItem(item.getItemStack()));

    }
    public HashMap<String, Kit> getKitsData() {
        return kitsData;
    }
}
