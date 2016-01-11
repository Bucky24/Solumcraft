package org.bukkit.inventory;

import org.bukkit.Material;
import org.spongepowered.api.Sponge;

/**
 * Created by solum on 12/30/2015.
 */
public class ItemStack {
    private org.spongepowered.api.item.inventory.ItemStack stack;

    public ItemStack(org.spongepowered.api.item.inventory.ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack(Material material) {
        org.spongepowered.api.item.inventory.ItemStack.Builder builder = Sponge.getRegistry().createBuilder(org.spongepowered.api.item.inventory.ItemStack.Builder.class);
        this.stack = builder.itemType(material.getValue().getType()).quantity(0).build();
    }

    public Material getType() {
        return Material.getValueOf(new ItemType(stack.getItem()));
    }

    public int getAmount() {
        return stack.getQuantity();
    }
}
