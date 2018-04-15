package org.bukkit.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.world.extent.Extent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by solum on 1/2/2016.
 */
public class Block {
    org.spongepowered.api.block.BlockSnapshot snapshot;

    private static Map<String, ItemType> materialOverrides;

    static {
        // Some blocks don't have a "type" that we can get, so instead I look up
        // type by name using this table
        materialOverrides = new HashMap<String, ItemType>();
        materialOverrides.put("minecraft:standing_sign", ItemTypes.SIGN);
        materialOverrides.put("minecraft:wall_sign", ItemTypes.SIGN);
    }

    public Block(org.spongepowered.api.block.BlockSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public Material getType() {
        org.spongepowered.api.block.BlockState state = snapshot.getState();
        //System.out.println(state.getType().getName());
        ItemType type = state.getType().getItem().orElse(null);
        if (type == null) {

            String typeName = state.getType().getName();
            if (materialOverrides.containsKey(typeName)) {
                type = materialOverrides.get(typeName);
            } else {
                System.out.println("Did not get a type for block state. Name is: " + typeName);
                return null;
            }
        }
        //System.out.println("Got a type of " + type.getName());
        return Material.getValueOf(new org.bukkit.inventory.ItemType(type));
    }

    public Location getLocation() {
        org.spongepowered.api.world.Location location = snapshot.getLocation().orElse(null);
        Extent extent = location.getExtent();
        //System.out.println("Extent is " + extent.getClass().getName());
        org.spongepowered.api.world.World world = (org.spongepowered.api.world.World)location.getExtent();
        return new Location(location, world);
    }
}
