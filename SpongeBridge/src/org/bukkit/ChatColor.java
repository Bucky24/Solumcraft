package org.bukkit;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by solum on 6/30/2015.
 */
public enum ChatColor {
    BLUE(TextColors.BLUE),
    RED(TextColors.RED),
    GREEN(TextColors.GREEN),
    DARK_GREEN(TextColors.DARK_GREEN),
    YELLOW(TextColors.YELLOW),
    LIGHT_PURPLE(TextColors.LIGHT_PURPLE),
    WHITE(TextColors.WHITE),
    GOLD(TextColors.GOLD),
    GRAY(TextColors.GRAY),
    BLACK(TextColors.BLACK),
    DARK_RED(TextColors.DARK_RED),
    AQUA(TextColors.AQUA),
    DARK_AQUA(TextColors.DARK_AQUA),
    DARK_BLUE(TextColors.DARK_BLUE),
    DARK_PURPLE(TextColors.DARK_PURPLE),
    DARK_GRAY(TextColors.DARK_GRAY);

    private final TextColor color;
    ChatColor(TextColor color) { this.color = color; }
    public TextColor getValue() { return color; }
    public String getStringValue() { return ":::" + color.getName() + ":::"; }
}
