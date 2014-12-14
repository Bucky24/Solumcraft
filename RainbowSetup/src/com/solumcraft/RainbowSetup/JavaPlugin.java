package com.solumcraft.RainbowSetup;

import PluginReference.MC_EventInfo;
import PluginReference.MC_Player;
import PluginReference.MC_Server;
import PluginReference.PluginBase;

import java.io.InputStream;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/13/14
 * Time: 6:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class JavaPlugin extends PluginBase {
    private MC_Server server;
    private static Logger logger;
    ////////////////////////////
    // Rainbow functions
    ////////////////////////////
    public void onStartup(MC_Server argServer){
        server = argServer;
        onEnable();
    }

    public void onPlayerInput(MC_Player plr, String msg, MC_EventInfo ei) {
        getLogger().info("Here");
        if (msg.startsWith("/")) {
            getLogger().info("Got command! " + msg);
            msg = msg.substring(1);
            String[] commandArr = msg.split(" ");
            String command = commandArr[0];
            commandArr = Arrays.copyOfRange(commandArr, 1, commandArr.length);
            onCommand((CommandSender)plr,new Command(command),command,commandArr);
        }
    }

    /////////////////////////////
    // Instance methods
    /////////////////////////////
    public Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public void saveDefaultConfig() {
        getLogger().warning("saveDefaultConfig is not implemented");
    }

    public ConfigHandler getConfig() {
        Class<?> callerClass = getClass();
        return new ConfigHandler(callerClass.getName());
    }

    ///////////////////////////////////////////////
    // Instance methods - overloadable
    ///////////////////////////////////////////////

    public void onEnable() {}

    public void onDisable() {}

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {return false;}
}
