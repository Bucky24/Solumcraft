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
        String argString = context.<String>getOne("args").or("");

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
