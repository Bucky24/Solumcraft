package org.bukkit.entity;

/**
 * Created by solum on 1/10/2016.
 */
public enum EntityType {
    BLUE(TextColors.BLUE),
    RED(TextColors.RED),
    GREEN(TextColors.GREEN);

    private final TextColor color;
    ChatColor(TextColor color) { this.color = color; }
    public TextColor getValue() { return color; }
}

