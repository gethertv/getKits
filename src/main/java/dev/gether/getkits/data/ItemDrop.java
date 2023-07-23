package dev.gether.getkits.data;

import org.bukkit.inventory.ItemStack;

public class ItemDrop {
    private int slot;
    private ItemStack itemStack;

    public ItemDrop(int slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
