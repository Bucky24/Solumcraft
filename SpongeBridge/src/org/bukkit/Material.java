package org.bukkit;

import SpongeBridge.Logger;
import org.bukkit.inventory.ItemType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by solum on 1/2/2016.
 */
public class Material {
    private static Logger logger;

    private static List<Material> materials;

    public static Material WOOD = new Material(ItemTypes.PLANKS);
    public static Material SAPLING = new Material(ItemTypes.SAPLING);
    public static Material STICK = new Material(ItemTypes.STICK);

    /*public static Material SPRUCE_PLANK = new Material(ItemTypes.PLANKS);

    public static Material ACACIA_DOOR = new Material(ItemTypes.ACACIA_DOOR);*/

    public static Material TNT = new Material(ItemTypes.TNT);

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

    public static Material ENCHANTED_BOOK = new Material(ItemTypes.ENCHANTED_BOOK);

    public static Material MONSTER_EGGS = new Material(ItemTypes.SPAWN_EGG);

    public static Material BEDROCK = new Material(ItemTypes.BEDROCK);

    public static void init(Logger logger) {
        Material.logger = logger;
        Material.materials = new ArrayList<Material>();

        Material[] values = staticValues();
        for (Material m : values) {
            materials.add(m);
        }
    }

    private ItemType type;

    Material(org.spongepowered.api.item.ItemType type) {
        this.type = new ItemType(type);
    }

    public ItemType getValue() {
        return type;
    }

    private static Material[] staticValues() {
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
        for (Material e : Material.materials) {
            if (e.getValue().equals(type)) {
                return e;
            }
        }
        Material m = new Material(type.getItemType());
        materials.add(m);

        return m;
    }

    public static Material getMaterial(String name) {
        for (Material e : Material.materials) {
            if (e.getValue().name().equals(name)) {
                return e;
            }
        }

        logger.warning("Unable to get valid material for name " + name);

        return null;
    }

    public String name() {
        return this.getValue().name();
    }

    public short getMaxDurability() {
        ItemStack stack = getValue().getItemType().getTemplate().createStack();
        if (stack.supports(Keys.ITEM_DURABILITY)) {
            Integer optional = stack.get(Keys.ITEM_DURABILITY).orElse(null);
            if (optional != null) {
                return optional.shortValue();
            }
        }
        return -1;
    }
}
