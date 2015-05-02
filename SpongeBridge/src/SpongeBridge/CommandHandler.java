package SpongeBridge;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;

import java.util.Collections;
import java.util.List;

/**
 * Created by solum on 5/2/2015.
 */
public class CommandHandler implements CommandCallable {
    String name;
    SpongeBridge plugin;
    private final String desc = "";
    private final String help = "";

    public CommandHandler(String name, SpongeBridge plugin) {
        this.name = name;
        this.plugin = plugin;
    }

    public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
        plugin.handleCommand(source,arguments);
        return Optional.of(CommandResult.empty());
    }

    public boolean testPermission(CommandSource source) {
        return source.hasPermission("example.exampleCommand");
    }

    public Optional<Text> getShortDescription(CommandSource source) {
        return SpongeText.getOptinalText(desc);
    }

    public Optional<Text> getHelp(CommandSource source) {
        return SpongeText.getOptinalText(help);
    }

    public Text getUsage(CommandSource source) {
        return SpongeText.getText("/<command> <message>");
    }

    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return Collections.emptyList();
    }
}
