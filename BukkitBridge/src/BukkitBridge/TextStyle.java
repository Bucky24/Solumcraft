package BukkitBridge;

import org.bukkit.ChatColor;

public enum TextStyle {
    OBFUSCATED(ChatColor.MAGIC),
    RESET(ChatColor.RESET);

    private final ChatColor style;
    TextStyle(ChatColor style) { this.style = style; }
    public ChatColor getValue() { return style; }
    public String getStringValue() { return ":::" + style.toString() + ":::"; }
}