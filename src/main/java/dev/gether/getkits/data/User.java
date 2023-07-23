package dev.gether.getkits.data;

import dev.gether.getkits.GetKits;
import dev.gether.getkits.manager.KitManager;
import dev.gether.getkits.utils.ColorFixer;
import dev.gether.getkits.utils.ItemUtils;
import dev.gether.getkits.utils.Timer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    private HashMap<String, Long> cooldown;
    private Inventory inventory;

    public User(HashMap<String, Long> cooldown) {
        this.cooldown = cooldown;
        FileConfiguration config = GetKits.getInstance().getConfig();
        this.inventory = Bukkit.createInventory(null, config.getInt("inv.size"), ColorFixer.addColors(config.getString("inv.title")));
        for (String name : GetKits.getInstance().getKitManager().getKitsData().keySet()) {
            cooldown.putIfAbsent(name, 0L);
        }
    }

    public User()
    {
        cooldown = new HashMap<>();
        FileConfiguration config = GetKits.getInstance().getConfig();
        this.inventory = Bukkit.createInventory(null, config.getInt("inv.size"), ColorFixer.addColors(config.getString("inv.title")));
        for (String name : GetKits.getInstance().getKitManager().getKitsData().keySet()) {
            cooldown.put(name, 0L);
        }
    }

    public void openInv(Player player, KitManager kitManager)
    {
        inventory.clear();
        ItemUtils.fillBackground(inventory, GetKits.getInstance().getConfig().getConfigurationSection("inv.backgrounds"));
        kitManager.getKitsData().forEach((name, kit) -> {
            if(!player.hasPermission(kit.getPermission()))
            {
                inventory.setItem(kit.getItemGUI().getSlot(), kit.getItemGUI().getNoAvailableItem());
                return;
            }
            if(!hasCooldown(kit))
            {
                inventory.setItem(kit.getItemGUI().getSlot(), kit.getItemGUI().getAvailableItem());
                return;
            }
            int cooldown = (int) ((getCooldown().get(kit.getKeyName())+kit.getCooldown()*1000L)-System.currentTimeMillis())/1000;
            ItemStack itemStack = kit.getItemGUI().getCooldownItem().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta.getLore()!=null)
            {
                List<String> lore = new ArrayList<>(itemMeta.getLore());
                for (int i = 0; i < lore.size(); i++) {
                    lore.set(i, lore.get(i).replace("{time}", Timer.getTime(cooldown)));
                }
                itemMeta.setLore(ColorFixer.addColors(lore));
            }
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(kit.getItemGUI().getSlot(), itemStack);
            return;
        });

        player.openInventory(inventory);
    }

    public boolean hasCooldown(Kit kit)
    {
        Long cooldownTime = cooldown.get(kit.getKeyName());
        if(cooldownTime==null)
            cooldownTime = 0L;

        long timeToRecived = cooldownTime + kit.getCooldown() * 1000;
        if(timeToRecived<=System.currentTimeMillis())
            return false;

        return true;
    }

    public HashMap<String, Long> getCooldown() {
        return cooldown;
    }

    public Inventory getInventory() {
        return inventory;
    }




}
