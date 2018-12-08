package BukkitBridge;

public class Player {
    org.bukkit.entity.Player player;

    Player(org.bukkit.entity.Player player) {
        this.player = player;
    }

    public static Player fromPlayer(org.bukkit.entity.Player player) {
        return new Player(player);
    }

    public org.bukkit.entity.Player getPlayer() {
        return this.player;
    }

    public org.bukkit.GameMode getGameMode() {
        return this.player.getGameMode();
    }

    public String getName() {
        return this.player.getName();
    }

    public void sendMessage(Text text) {
        this.player.sendMessage(text.getPlainText());
    }
}
