package org.bukkit;

import org.bukkit.inventory.ItemType;
import org.spongepowered.api.item.ItemTypes;

/**
 * Created by solum on 1/2/2016.
 */
public enum Material {
    ACACIA_DOOR(ItemTypes.ACACIA_DOOR),

    AIR(ItemTypes.NONE),

    WOOD_AXE(ItemTypes.WOODEN_AXE),
    WOOD_HOE(ItemTypes.WOODEN_HOE),
    WOOD_PICKAXE(ItemTypes.WOODEN_PICKAXE),
    WOOD_SPADE(ItemTypes.WOODEN_SHOVEL),
    STONE_AXE(ItemTypes.STONE_AXE),
    STONE_HOE(ItemTypes.STONE_HOE),
    STONE_PICKAXE(ItemTypes.STONE_PICKAXE),
    STONE_SPADE(ItemTypes.STONE_SHOVEL),
    IRON_AXE(ItemTypes.IRON_AXE),
    IRON_HOE(ItemTypes.IRON_HOE),
    IRON_PICKAXE(ItemTypes.IRON_PICKAXE),
    IRON_SPADE(ItemTypes.IRON_SHOVEL),
    GOLD_AXE(ItemTypes.GOLDEN_AXE),
    GOLD_HOE(ItemTypes.GOLDEN_HOE),
    GOLD_PICKAXE(ItemTypes.GOLDEN_PICKAXE),
    GOLD_SPADE(ItemTypes.GOLDEN_SHOVEL),
    DIAMOND_AXE(ItemTypes.DIAMOND_AXE),
    DIAMOND_HOE(ItemTypes.DIAMOND_HOE),
    DIAMOND_PICKAXE(ItemTypes.DIAMOND_PICKAXE),
    DIAMOND_SPADE(ItemTypes.DIAMOND_SHOVEL);

    private final ItemType type;
    Material(org.spongepowered.api.item.ItemType type) { this.type = new ItemType(type); }
    public ItemType getValue() { return type; }
    public static Material getValueOf(ItemType type) {
        for (Material e : Material.values()) {
            if (e.getValue().equals(type)) {
                return e;
            }
        }
        return null;
    }

    public static Material getMaterial(String name) {
        for (Material e : Material.values()) {
            if (e.getValue().name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
