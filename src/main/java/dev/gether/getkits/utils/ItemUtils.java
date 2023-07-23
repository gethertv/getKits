package dev.gether.getkits.utils;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    public static ItemStack getItemFromConfig(ConfigurationSection section)
    {
        ItemStack itemStack = new ItemStack(Material.valueOf(section.getString(".material").toUpperCase()));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ColorFixer.addColors(section.getString(".displayname")));
        List<String> lore = new ArrayList<>(section.getStringList(".lore"));
        itemMeta.setLore(ColorFixer.addColors(lore));

        itemStack.setItemMeta(itemMeta);

        return itemStack;

    }

    public static Inventory fillBackground(Inventory inventory, ConfigurationSection section)
    {
        for(String key : section.getKeys(false))
        {
            ConfigurationSection bgItemSection = section.getConfigurationSection("."+key);
            ItemStack itemStack = getItemFromConfig(bgItemSection);
            List<Integer> slots = new ArrayList<>(bgItemSection.getIntegerList(".slots"));
            for(int slot : slots)
                inventory.setItem(slot, itemStack);
        }

        return inventory;
    }
}
