package com.thepastimers.Login;

import com.thepastimers.Database.Database;
import com.thepastimers.Permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/24/13
 * Time: 6:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class Login extends JavaPlugin implements Listener {
    Database database;
    Permission permission;

    @Override
    public void onEnable() {
        getLogger().info("Login init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Unable to load Database plugin. Some functionality may not be available");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Unable to load Permission plugin. Some functionality may not be available");
        }

        getLogger().info(LoginData.getTableInfo());

        getLogger().info("Login init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Login disable");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String playerName = "";

        if (sender instanceof Player) {
            playerName = ((Player)sender).getName();
        } else {
            playerName = "CONSOLE";
        }

        String command = cmd.getName();

        if (command.equalsIgnoreCase("setPassword")) {
            if (permission == null || !permission.hasPermission(playerName,"login_set") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (login_set)");
                return true;
            }

            if (args.length > 0) {
                String passwd = args[0];

                LoginData data = null;

                List<LoginData> dataList = (List<LoginData>)database.select(LoginData.class,"player = '" + database.makeSafe(playerName) + "'");
                if (dataList.size() > 0) {
                    data = dataList.get(0);
                }

                if (data == null) {
                    data = new LoginData();
                }

                data.setPlayer(playerName);
                data.setPassword(passwd);

                if (!data.save(database)) {
                    sender.sendMessage(ChatColor.RED + "Unable to set login");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Login set!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "/setPassword <password. Do not use your minecraft password>");
            }
        } else {
            return false;
        }

        return true;
    }
}
