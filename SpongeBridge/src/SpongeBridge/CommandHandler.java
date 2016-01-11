package SpongeBridge;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;

import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solum on 5/2/2015.
 */
public class CommandHandler implements CommandExecutor {
    String name;
    SpongeBridge plugin;

    public CommandHandler(String name, SpongeBridge plugin) {
        this.name = name;
        this.plugin = plugin;

        CommandSpec spec = CommandSpec.builder()
                .description(Text.of("Description"))
                .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("args"))))
                .executor(this)
                .build();
        plugin.game.getCommandManager().register(plugin, spec, name);
    }

    public CommandResult execute(CommandSource source, CommandContext context) {
        String argString = context.<String>getOne("args").orElse("");

        String[] argList = argString.split(" ");
        List<String> finalArgs = new ArrayList<String>();
        for (String arg : argList) {
            if (!"".equals(arg)) {
                finalArgs.add(arg);
            }
        }

        boolean result = plugin.handleCommand(source,name,finalArgs.toArray(new String[finalArgs.size()]));
        if (result) {
            return CommandResult.success();
        } else {
            return CommandResult.empty();
        }
    }
}
