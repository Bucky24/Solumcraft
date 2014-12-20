package com.thepastimers.Database;

import RainbowSetup.Command;
import RainbowSetup.CommandSender;
import RainbowSetup.Player;
import RainbowSetup.JavaPlugin;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/1/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */

@SuppressWarnings("unchecked")

public class Database extends JavaPlugin {
    boolean enabled = true;
    String url;
    String username;
    String password;
    boolean debugMode = false;

    Connection connection;

    ResultSet generatedKeys;

    Map<String,Integer> queryLog;

    int connections = 0;
    int totalConn = 0;

    @Override
    public void onEnable() {
        getLogger().info("Database init");

        getLogger().info("Loading driver");
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            getLogger().warning("Unable to load driver");
            enabled = false;
        }
getLogger().info("We're now here!");
        saveDefaultConfig();
        getLogger().info("Saved default config! getting new config!");
        password = getConfig().getString("password");
        username = getConfig().getString("username");
        String host = getConfig().getString("server");
        String port = getConfig().getString("port");
        String database = getConfig().getString("database");
        url = "jdbc:mysql://" + host + ":" + port + "/" + database;

        getLogger().info("Making connection");
        try {
            connection = DriverManager.getConnection(url,username,password);
            connections ++;
            //totalConn ++;
            killConnection(connection);
        } catch (Exception e) {
            getLogger().warning("Unable to connect to database!");
            getLogger().logError(e);
            enabled = false;
        }

        generatedKeys = null;
        queryLog = new HashMap<String, Integer>();

        if (enabled) {
            getLogger().info("Database init successful");
        } else {
            getLogger().warning("Database init had errors. Database functionality disabled.");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Database disabled");
        killConnection(connection);
    }

    public List<? extends Table> select(Class c, String where) {
        return select(c,where,true);
    }

    public List<? extends Table> select(Class c, String where, boolean cache) {
        List<? extends Table> ret = new ArrayList<Table>();
        if (!enabled) {
            return ret;
        }
        if (c == null || c.getSuperclass() != Table.class || where == null) {
            getLogger().warning("Invalid inputs passed to select");
            return ret;
        }
        String table = "";
        try {
            table = (String)c.getField("table").get(null);
        } catch (Exception e) {
            getLogger().warning("select: Could not get 'table' for " + c.getName());
            return ret;
        }

        if (where == "") {
            where = "1";
        }

        if (debugMode) {
            int count = 0;
            if (queryLog.containsKey(c.getName())) {
                count = queryLog.get(c.getName());
            }
            count ++;
            queryLog.put(c.getName(),count);
        }

        String query = "SELECT ";
        if (!cache) {
            query += "SQL_NO_CACHE ";
        }
        query += "* FROM " + table + " WHERE " + where;

        /*try {
            connection = DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            getLogger().warning("select: Unable to connect to database");
            return ret;
        }*/

        try {
            //if (connection.isClosed()) {
                //getLogger().warning("Connection closed, attempting reconnect");
            killConnection(connection);
            connection = DriverManager.getConnection(url,username,password);
            connections ++;
            //totalConn ++;
            //}
        } catch (Exception e) {
            getLogger().warning("Unable to re-open connection");
            getLogger().warning(e.getMessage());
            return ret;
        }

        try {
            Statement statement = connection.createStatement();
            //getLogger().info(query);
            ResultSet results = statement.executeQuery(query);

            try {
                Class[] argTypes = new Class[] {ResultSet.class};
                Method m = c.getDeclaredMethod("parseResult",argTypes);
                ret = (List<? extends Table>)m.invoke(null,results);
                killConnection(connection);

                //for (Table t : ret) {
                //    getLogger().info("About to return data: " + t.getId());
                //}

                return ret;
            } catch (NoSuchMethodException e) {
                //getLogger().warning("No such method: parseResult for class " + c.getName() + ".");
                try {
                    // try it with Table
                    Class[] argTypes = new Class[] {ResultSet.class,Class.class};
                    Method m = Table.class.getDeclaredMethod("parseResult",argTypes);
                    ret = (List<? extends Table>)m.invoke(null,results,c);
                    killConnection(connection);

                    return ret;
                } catch (Exception e2) {
                    getLogger().warning("select: Unable to invoke parseResult for " + c.getName());
                    e2.printStackTrace();
                    killConnection(connection);
                    return ret;
                }
            } catch (Exception e) {
                getLogger().warning("Could not call parseResult for class " + c.getName() + ".");
                e.printStackTrace();
                return ret;
            }
        } catch (Exception e) {
            getLogger().warning("select: Unable to run query:");
            getLogger().warning(query);
            getLogger().warning(e.getMessage());
            //e.printStackTrace();
            getLogger().warning("Closing connection.");
            killConnection(connection);
            return ret;
        }

    }

    public ResultSet rawSelect(String query) {
        if (!enabled) {
            return null;
        }
        try {
            killConnection(connection);
            connection = DriverManager.getConnection(url,username,password);
            connections ++;
            //totalConn ++;
        } catch (Exception e) {
            getLogger().warning("Unable to re-open connection");
            getLogger().warning(e.getMessage());
            return null;
        }

        if (debugMode) {
            int count = 0;
            if (queryLog.containsKey("raw")) {
                count = queryLog.get("raw");
            }
            count ++;
            queryLog.put("raw",count);
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);

            return results;
        } catch (Exception e) {
            getLogger().warning("select: Unable to run query:");
            getLogger().warning(query);
            getLogger().warning(e.getMessage());
            //e.printStackTrace();
            getLogger().warning("Closing connection.");
            killConnection(connection);
            return null;
        }

    }

    private void killConnection(Connection c) {
        try {
            if (c.isClosed()) return;
            c.close();
            connections --;
        } catch (Exception e) {
            getLogger().warning("Cannot close connection");
        }
    }

    public boolean query(String query) {
        if (!enabled) {
            return false;
        }

        if (query == null) {
            getLogger().warning("Invalid input passed to query");
            return false;
        }

        /*try {
            connection = DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            getLogger().warning("query: Unable to connect to database");
            return false;
        }*/

        if (debugMode) {
            int count = 0;
            if (queryLog.containsKey("query")) {
                count = queryLog.get("query");
            }
            count ++;
            queryLog.put("query",count);
        }

        try {
            //if (connection.isClosed()) {
                //getLogger().warning("Connection closed, attempting reconnect");
                killConnection(connection);
                connection = DriverManager.getConnection(url,username,password);
                connections ++;
                //totalConn ++;
            //}
        } catch (Exception e) {
            getLogger().warning("Unable to re-open connection");
            getLogger().warning(e.getMessage());
            return false;
        }

        try {
            Statement statement = connection.createStatement();
            statement.execute(query,Statement.RETURN_GENERATED_KEYS);
            generatedKeys = statement.getGeneratedKeys();
            killConnection(connection);
        } catch (Exception e) {
            getLogger().warning("query: Unable to run query:");
            getLogger().warning(query);
            getLogger().info(e.getMessage());
            //e.printStackTrace();
            getLogger().warning("Closing connection.");
            killConnection(connection);
            return false;
        }

        return true;
    }

    public String makeSafe(String val) {
        if (val == null) {
            return "";
        }
        String ret = val;
        ret = ret.replace("\\","\\\\");
        ret = ret.replace("'","\\'");

        return ret;
    }

    public ResultSet getGeneratedKeys() {
        return generatedKeys;
    }

    public boolean createTableIfNotExists(String table, String definition) {
        try {
            ResultSet set = rawSelect("SHOW TABLES LIKE '" + makeSafe(table) + "';");
            int count = 0;
            while (set.next()) {
                count ++;
            }
            killConnection(connection);
            if (count == 0) {
                getLogger().info("Unable to find database table " + table + ", attempting to create");
                boolean result = query(definition);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
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

        if ("db".equalsIgnoreCase(command)) {
            if (!"CONSOLE".equalsIgnoreCase(playerName)) {
                sender.sendMessage("This command is only available in console");
                return true;
            }

            if (args.length > 0) {
                String subCommand = args[0];

                if ("clearLog".equalsIgnoreCase(subCommand)) {
                    queryLog.clear();
                } else if ("showLog".equalsIgnoreCase(subCommand)) {
                    sender.sendMessage("DB query log:");
                    for (String key : queryLog.keySet()) {
                        sender.sendMessage(key + ": " + queryLog.get(key));
                    }
                } else if ("connections".equalsIgnoreCase(subCommand)) {
                    sender.sendMessage("Current open connections: " + connections);
                    sender.sendMessage("Total connections: " + totalConn);
                } else if ("debug".equalsIgnoreCase(subCommand)) {
                    if (args.length > 1) {
                        if ("on".equalsIgnoreCase(args[1])) {
                            debugMode = true;
                        } else if ("off".equalsIgnoreCase(args[1])) {
                            debugMode = false;
                        }
                    } else {
                        sender.sendMessage("/db debug <on|off>");
                    }
                    if (debugMode) {
                        sender.sendMessage("Debug mode is on");
                    } else {
                        sender.sendMessage("Debug mode is off");
                    }
                }
            } else {
                sender.sendMessage("/db <clearLog|showLog|connections|debug>");
            }
        } else {
            return false;
        }
        return true;
    }
}
