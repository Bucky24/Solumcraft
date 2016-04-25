package org.bukkit.inventory;

import SpongeBridge.Logger;
import SpongeBridge.SpongeBridge;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;

import java.util.Optional;

/**
 * Created by solum on 12/30/2015.
 */
public class ItemStack {
    private static Logger logger;

    public static void init(Logger logger) {
        ItemStack.logger = logger;
    }

    private org.spongepowered.api.item.inventory.ItemStack stack;

    public ItemStack(org.spongepowered.api.item.inventory.ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack(Material mat, int amount) {
        org.spongepowered.api.item.inventory.ItemStack.Builder builder = Sponge.getRegistry().createBuilder(org.spongepowered.api.item.inventory.ItemStack.Builder.class);
        this.stack = builder.itemType(mat.getValue().getItemType()).quantity(amount).build();
    }

    public ItemStack(Material material) {
        org.spongepowered.api.item.inventory.ItemStack.Builder builder = Sponge.getRegistry().createBuilder(org.spongepowered.api.item.inventory.ItemStack.Builder.class);
        this.stack = builder.itemType(material.getValue().getItemType()).quantity(1).build();
    }

    public Material getType() {
        return Material.getValueOf(new ItemType(stack.getItem()));
    }

    public org.spongepowered.api.item.inventory.ItemStack getStack() {
        return this.stack;
    }

    public int getAmount() {
        return stack.getQuantity();
    }

    public short getDurability() {
        if (stack.supports(Keys.ITEM_DURABILITY)) {
            Integer optional = stack.get(Keys.ITEM_DURABILITY).orElse(null);
            if (optional != null) {
                return optional.shortValue();
            }
        }
        DataContainer cont = stack.toContainer();
        DataQuery query = DataQuery.of('/',"UnsafeDamage");
        if (!cont.contains(query)) {
            return -1;
        } else {
            Integer variant = (Integer)cont.get(query).orElse(-1);
            return variant.shortValue();
        }
    }

    public void setDurability(short durability) {
        if (stack.supports(Keys.ITEM_DURABILITY)) {
            stack.offer(Keys.ITEM_DURABILITY, (int)durability);
        } else {
            SpongeBridge.logger.warning("Unable to set durability on item " + stack.getItem().getName() + ": does not support the key");
        }

        if (this.getType().name().equals("minecraft:spawn_egg")) {
            SpongeBridge.logger.info("got a spawn egg");
            EntityType type = EntityType.getForDurability(durability);
            if (type == null) {
                SpongeBridge.logger.warning("Attempting to set spawn egg type to " + durability + ", but EntityType is not aware of it");
            } else {
                stack.offer(Keys.SPAWNABLE_ENTITY_TYPE, type.getValue());
            }
        }
    }

    public int getMaxStackSize() {
        return stack.getMaxStackQuantity();
    }

    public void setAmount(int amount) {
        stack.setQuantity(amount);
    }

    public Byte getData() {
        return 0;
    }

    public void setData(Byte b) {

    }

    public ItemMeta getItemMeta() {
        return null;
    }

    public void setItemMeta(ItemMeta meta) {

    }

    public String getSpongeName() {
        return stack.getItem().getName();
    }
}
