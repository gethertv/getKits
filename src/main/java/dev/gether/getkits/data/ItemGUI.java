package dev.gether.getkits.data;

import org.bukkit.inventory.ItemStack;

public class ItemGUI {

    private int slot;
    private ItemStack availableItem;
    private ItemStack noAvailableItem;
    private ItemStack cooldownItem;

    public ItemGUI(int slot, ItemStack availableItem, ItemStack noAvailableItem, ItemStack cooldownItem) {
        this.slot = slot;
        this.availableItem = availableItem;
        this.noAvailableItem = noAvailableItem;
        this.cooldownItem = cooldownItem;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getAvailableItem() {
        return availableItem;
    }

    public ItemStack getNoAvailableItem() {
        return noAvailableItem;
    }

    public ItemStack getCooldownItem() {
        return cooldownItem;
    }
}
