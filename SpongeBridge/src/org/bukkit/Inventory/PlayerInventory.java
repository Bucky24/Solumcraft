package org.bukkit.inventory;

import com.google.common.collect.Iterables;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.util.*;

/**
 * Created by solum on 12/30/2015.
 */
public class PlayerInventory {
    private CarriedInventory<? extends Carrier> inventory;

    public PlayerInventory(CarriedInventory<? extends Carrier> inventory) {
        this.inventory = inventory;
    }

    public ItemStack[] getContents() {
        List<ItemStack> stackList = new ArrayList<ItemStack>();

        Iterable<Slot> slots = getSlots(inventory);
        for (Slot slot : slots) {
            org.spongepowered.api.item.inventory.ItemStack stack = slot.peek().orElse(null);
            if (stack != null) {
                stackList.add(new ItemStack(stack));
            }
        }

        return (ItemStack[])stackList.toArray();
    }

    private static Iterable<Slot> getSlots(Inventory inventory) {
        if (inventory instanceof Slot) {
            return Collections.emptyList();
        }
        Iterable<Slot> slots = inventory.slots();
        for (Inventory subInventory : inventory) {
            Iterables.concat(slots, getSlots(subInventory));
        }
        return slots;
    }

    public Iterator<ItemStack> iterator() {
        return Arrays.asList(this.getContents()).iterator();
    }
}
