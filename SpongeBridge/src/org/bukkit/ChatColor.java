package org.bukkit;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by solum on 6/30/2015.
 */
public enum ChatColor {
    BLUE(TextColors.BLUE),
    RED(TextColors.RED);

    private final TextColor color;
    ChatColor(TextColor color) { this.color = color; }
    public TextColor getValue() { return color; }
}
