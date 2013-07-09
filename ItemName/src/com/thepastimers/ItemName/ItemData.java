package com.thepastimers.ItemName;

import org.bukkit.Material;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 3/2/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ItemData {
    String realName;
    Material material;
    short durability;
    boolean useDurability;

    public ItemData(String rn, Material m) {
        realName = rn;
        material = m;
        durability = 0;
        useDurability = false;
    }

    public ItemData(String rn, Material m, short d) {
        realName = rn;
        material = m;
        durability = d;
        useDurability = true;
    }

    public ItemData(String rn, Material m, int d) {
        realName = rn;
        material = m;
        durability = (short)d;
        useDurability = true;
    }

    public boolean compare(String rn) {
        if (rn == null) {
            return false;
        }

        return (rn.equalsIgnoreCase(realName));
    }

    public boolean compare(Material m, short d) {
        if (m == null) {
            return false;
        }

        return (m.equals(material) && (!useDurability || d == durability));
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public short getDurability() {
        return durability;
    }

    public void setDurability(short durability) {
        this.durability = durability;
    }

    public boolean isUseDurability() {
        return useDurability;
    }

    public void setUseDurability(boolean useDurability) {
        this.useDurability = useDurability;
    }
}
