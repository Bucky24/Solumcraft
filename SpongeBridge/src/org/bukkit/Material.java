package org.bukkit;

import SpongeBridge.Logger;
import org.bukkit.inventory.ItemType;
import org.spongepowered.api.item.ItemTypes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by solum on 1/2/2016.
 */
public class Material {
    private static Logger logger;

    public static Material OAK_PLANK = new Material(ItemTypes.PLANKS,0);
    public static Material SPRUCE_PLANK = new Material(ItemTypes.PLANKS,1);

    public static Material ACACIA_DOOR = new Material(ItemTypes.ACACIA_DOOR);

    public static Material AIR = new Material(ItemTypes.NONE);

    public static Material WOOD_AXE = new Material(ItemTypes.WOODEN_AXE);
    public static Material WOOD_HOE = new Material(ItemTypes.WOODEN_HOE);
    public static Material WOOD_PICKAXE = new Material(ItemTypes.WOODEN_PICKAXE);
    public static Material WOOD_SPADE = new Material(ItemTypes.WOODEN_SHOVEL);
    public static Material STONE_AXE = new Material(ItemTypes.STONE_AXE);
    public static Material STONE_HOE = new Material(ItemTypes.STONE_HOE);
    public static Material STONE_PICKAXE = new Material(ItemTypes.STONE_PICKAXE);
    public static Material STONE_SPADE = new Material(ItemTypes.STONE_SHOVEL);
    public static Material IRON_AXE = new Material(ItemTypes.IRON_AXE);
    public static Material IRON_HOE = new Material(ItemTypes.IRON_HOE);
    public static Material IRON_PICKAXE = new Material(ItemTypes.IRON_PICKAXE);
    public static Material IRON_SPADE = new Material(ItemTypes.IRON_SHOVEL);
    public static Material GOLD_AXE = new Material(ItemTypes.GOLDEN_AXE);
    public static Material GOLD_HOE = new Material(ItemTypes.GOLDEN_HOE);
    public static Material GOLD_PICKAXE = new Material(ItemTypes.GOLDEN_PICKAXE);
    public static Material GOLD_SPADE = new Material(ItemTypes.GOLDEN_SHOVEL);
    public static Material DIAMOND_AXE = new Material(ItemTypes.DIAMOND_AXE);
    public static Material DIAMOND_HOE = new Material(ItemTypes.DIAMOND_HOE);
    public static Material DIAMOND_PICKAXE = new Material(ItemTypes.DIAMOND_PICKAXE);
    public static Material DIAMOND_SPADE = new Material(ItemTypes.DIAMOND_SHOVEL);
    public static Material DIAMOND_SWORD = new Material(ItemTypes.DIAMOND_SWORD);

    static Material ENCHANTED_BOOK = new Material(ItemTypes.ENCHANTED_BOOK);

    public static void init(Logger logger) {
        Material.logger = logger;
    }

    private ItemType type;
    private int durability;
    private boolean useDurability;

    Material(org.spongepowered.api.item.ItemType type) {
        this.type = new ItemType(type);
        useDurability = false;
    }

    Material(org.spongepowered.api.item.ItemType type, int durability) {
        this.type = new ItemType(type);
        this.durability = durability;
        useDurability = true;
    }

    public ItemType getValue() {
        return type;
    }

    public int getDurability() {
        return durability;
    }

    private static Material[] values() {
        Field[] declaredFields = Material.class.getDeclaredFields();
        List<Material> staticFields = new ArrayList<Material>();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                try {
                    Object val = field.get(null);
                    if (val instanceof Material) {
                        staticFields.add((Material)val);
                    }
                } catch (Exception e) {
                    // do nothing basically
                }
            }
        }
        Material[] mat = new Material[staticFields.size()];
        for (int i=0;i<staticFields.size();i++) {
            mat[i] = staticFields.get(i);
        }

        return mat;
    }

    public static Material getValueOf(ItemType type) {
        for (Material e : Material.values()) {
            if (e.getValue().equals(type)) {
                return e;
            }
        }
        logger.warning("Unable to get valid material for item type with name " + type.name());
        return null;
    }

    public static Material getValueOf(ItemType type, int durability) {
        for (Material e : Material.values()) {
            if (e.getValue().equals(type) && (e.useDurability && e.getDurability() == durability)) {
                return e;
            }
        }
        logger.warning("Unable to get valid material for item type with name " + type.name());
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

    public String name() {
        return this.getValue().name();
    }
}
