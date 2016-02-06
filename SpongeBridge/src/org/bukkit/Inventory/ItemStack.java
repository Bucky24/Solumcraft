package org.bukkit.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.block.trait.EnumTraits;
import org.spongepowered.api.data.key.Keys;

import java.util.Optional;

/**
 * Created by solum on 12/30/2015.
 */
public class ItemStack {
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
        this.stack = builder.itemType(material.getValue().getItemType()).quantity(0).build();
    }

    public Material getType() {
        return Material.getValueOf(new ItemType(stack.getItem()),getVariant());
    }

    public org.spongepowered.api.item.inventory.ItemStack getStack() {
        return this.stack;
    }

    public int getAmount() {
        return stack.getQuantity();
    }

    public String getVariant() {
        BlockType type = stack.getItem().getBlock().orElse(null);
        if (type == null) {
            return "";
        }
        BlockTrait trait = type.getTrait(EnumTraits.MONSTER_EGG_VARIANT.toString()).orElse(null);
        if (trait == null) {
            return "";
        }
        Optional opt = type.getDefaultState().getTraitValue(trait);
        if (!opt.isPresent()) {
            return "";
        }
        Object obj = opt.get();
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    public short getDurability() {
        if(stack.supports(Keys.ITEM_DURABILITY)) {
            Integer optional = stack.get(Keys.ITEM_DURABILITY).orElse(-1);
            return optional.shortValue();
        }
        return -1;
    }

    public void setDurability(short durability) {
        if (stack.supports(Keys.ITEM_DURABILITY)) {
            stack.offer(Keys.ITEM_DURABILITY, (int)durability);
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
