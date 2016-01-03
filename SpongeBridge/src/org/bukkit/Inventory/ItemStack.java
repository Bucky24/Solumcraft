package org.bukkit.inventory;

/**
 * Created by solum on 12/30/2015.
 */
public class ItemStack {
    private org.spongepowered.api.item.inventory.ItemStack stack;

    public ItemStack(org.spongepowered.api.item.inventory.ItemStack stack) {
        this.stack = stack;
    }

    public ItemType getType() {
        return new ItemType(stack.getItem());
    }

    public int getAmount() {
        return stack.getQuantity();
    }
}
