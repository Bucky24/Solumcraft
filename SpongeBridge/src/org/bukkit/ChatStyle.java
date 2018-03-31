package org.bukkit;

import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

public enum ChatStyle {
    OBFUSCATED(TextStyles.OBFUSCATED),
    RESET(TextStyles.RESET);

    private final TextStyle style;
    ChatStyle(TextStyle style) { this.style = style; }
    public TextStyle getValue() { return style; }
    public String getStringValue() { return ":::" + style.toString() + ":::"; }
}