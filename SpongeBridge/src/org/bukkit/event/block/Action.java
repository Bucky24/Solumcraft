package org.bukkit.event.block;

public enum Action {
    LEFT_CLICK_BLOCK(1),
    RIGHT_CLICK_BLOCK(2);

    private final Integer type;
    Action(Integer type) { this.type = type; }
    public Integer getValue() { return type; }
}
