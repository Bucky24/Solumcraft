package SpongeBridge;

import org.bukkit.command.Command;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.args.CommandElement;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by solum on 5/2/2015.
 */
public class CommandHandler implements CommandExecutor {
    String name;
    SpongeBridge plugin;
    private final String desc = "";
    private final String help = "";

    private final int arguments = 20;

    public CommandHandler(String name, SpongeBridge plugin) {
        this.name = name;
        this.plugin = plugin;

        CommandSpec spec = CommandSpec.builder()
                .description(Texts.of("Description"))
                .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("args"))))
                .executor(this)
                .build();
        plugin.game.getCommandDispatcher().register(plugin, spec, name);
    }

    public CommandResult execute(CommandSource source, CommandContext context) {
        String argString = context.<String>getOne("args").orNull();
        plugin.getLogger().info("Argument is " + argString);
        if (argString == null) {
            argString = "";
        }

        plugin.handleCommand(source,name,argString.split(" "));
        return CommandResult.success();
    }

    public boolean testPermission(CommandSource source) {
        return source.hasPermission("example.exampleCommand");
    }

    public Optional<Text> getShortDescription(CommandSource source) {
        return SpongeText.getOptionalText(desc);
    }

    public Optional<Text> getHelp(CommandSource source) {
        return SpongeText.getOptionalText(help);
    }

    public Text getUsage(CommandSource source) {
        return SpongeText.getText("/<command> <message>");
    }

    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return Collections.emptyList();
    }
}
