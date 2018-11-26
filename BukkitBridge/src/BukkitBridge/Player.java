package BukkitBridge;

public class Player {
    org.bukkit.entity.Player player;

    Player(org.bukkit.entity.Player player) {
        this.player = player;
    }

    public void sendMessage(Text text) {
        this.player.sendMessage(text.getPlainText());
    }
}
