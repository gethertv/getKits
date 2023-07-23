package dev.gether.getkits.data;

import org.bukkit.inventory.Inventory;

public class AdminUser {
    private String key;
    private Inventory inventory;

    public AdminUser(String key, Inventory inventory) {
        this.key = key;
        this.inventory = inventory;
    }

    public String getKey() {
        return key;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
