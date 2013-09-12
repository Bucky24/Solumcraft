package com.thepastimers.Inventory;

import com.thepastimers.Database.Table;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 9/12/13
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class InventoryItem extends Table {
    public static String table = "inventory";

    String invName; // generic code
    String slot;
    String item;
    int amount;
    int durability;
    String enchants;

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public String getEnchants() {
        return enchants;
    }

    public void setEnchants(String enchants) {
        this.enchants = enchants;
    }
}
