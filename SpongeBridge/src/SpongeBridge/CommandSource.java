package SpongeBridge;

import org.bukkit.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by solum on 7/3/2015.
 */
public class CommandSource extends Player {
    org.spongepowered.api.command.CommandSource sender;

    public CommandSource(org.spongepowered.api.command.CommandSource sender) throws Exception {
        this.sender = sender;
    }

    public void sendMessage(String message) {
        this.sender.sendMessage(SpongeText.getText(message));
    }

    public void sendMessage(Text text) {
        this.sender.sendMessage(SpongeText.getText(text));
    }
}
