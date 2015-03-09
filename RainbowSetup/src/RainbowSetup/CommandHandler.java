package RainbowSetup;

import PluginReference.MC_Command;
import PluginReference.MC_Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solum on 3/8/2015.
 */
public class CommandHandler implements MC_Command {
    String name;
    MyPlugin plugin;

    public CommandHandler(String name, MyPlugin plugin) {
        this.name = name;
        this.plugin = plugin;
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<String>();
    }

    @Override
    public boolean hasPermissionToUse(MC_Player player) {
        return true;
    }

    @Override
    public String getCommandName() {
        return name;
    }

    @Override
    public void handleCommand(MC_Player player, String[] args) {
        plugin.handleCommand(player,name,args);
    }

    @Override
    public String getHelpLine(MC_Player player) {
        return "No help available for this command";
    }

    @Override
    public List<String> getTabCompletionList(MC_Player player, String[] var2) {
        return new ArrayList<String>();
    }
}
