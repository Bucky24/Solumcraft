package org.bukkit.plugin.java;


import SpongeBridge.PluginDescription;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/13/14
 * Time: 6:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class JavaPlugin {
    /*private static Logger logger;
    public MyPlugin server;*/
    public Class myClass;
    public PluginDescription description;

    /////////////////////////////
    // Instance methods
    /////////////////////////////
   /* public Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public void saveDefaultConfig() {
        Class<?> callerClass = getClass();
        ConfigHandler handler = new ConfigHandler(callerClass.getSimpleName(),logger);
        handler.createDefaultConfig(callerClass.getSimpleName(),logger);
    }

    public ConfigHandler getConfig() {
        Class<?> callerClass = getClass();
        return new ConfigHandler(callerClass.getSimpleName(),logger);
    }

    public MyPlugin getServer() {
        return server;
    }

    ///////////////////////////////////////////////
    // Instance methods - overloadable
    ///////////////////////////////////////////////

    public void onEnable() {}

    public void onDisable() {}

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {return false;}*/
}
