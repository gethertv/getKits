package dev.gether.getkits.data;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class Kit {

    private String keyName;
    private String name;
    private String permission;
    private int cooldown;
    private int slot;
    private List<ItemDrop> kitItems;
    private Inventory inventory;
    private ItemGUI itemGUI;

    public Kit(String keyName, String name, String permission, int cooldown, int slot, List<ItemDrop> kitItems, Inventory inventory, ItemGUI itemGUI) {
        this.keyName = keyName;
        this.name = name;
        this.permission = permission;
        this.cooldown = cooldown;
        this.slot = slot;
        this.kitItems = kitItems;
        this.inventory = inventory;
        this.itemGUI = itemGUI;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public int getCooldown() {
        return cooldown;
    }

    public List<ItemDrop> getKitItems() {
        return kitItems;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemGUI getItemGUI() {
        return itemGUI;
    }

    public int getSlot() {
        return slot;
    }
}
