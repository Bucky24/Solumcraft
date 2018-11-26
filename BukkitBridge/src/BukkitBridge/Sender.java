package BukkitBridge;

import org.bukkit.command.CommandSender;

public class Sender {
    private CommandSender sender;

    public Sender(CommandSender sender) {
        this.sender = sender;
    }

    public static Sender fromSender(CommandSender sender) {
        return new Sender(sender);
    }

    public void sendMessage(Text text) {
        this.sender.sendMessage(text.getPlainText());
    }
}
