package com.thepastimers.Database;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/1/12
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class Database extends JavaPlugin {
    boolean enabled = true;
    String url;
    String username;
    String password;

    Connection connection;

    ResultSet generatedKeys;

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

        saveDefaultConfig();
        password = getConfig().getString("password");
        username = getConfig().getString("username");
        String host = getConfig().getString("server");
        String port = getConfig().getString("port");
        String database = getConfig().getString("database");
        url = "jdbc:mysql://" + host + ":" + port + "/" + database;

        getLogger().info("Making connection");
        try {
            connection = DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            getLogger().warning("Unable to connect to database!");
            e.printStackTrace();
            enabled = false;
        }

        generatedKeys = null;

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
            //}
        } catch (Exception e) {
            getLogger().warning("Unable to re-open connection");
            getLogger().warning(e.getMessage());
            return ret;
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);

            try {
                Class[] argTypes = new Class[] {ResultSet.class};
                Method m = c.getDeclaredMethod("parseResult",argTypes);
                ret = (List<? extends Table>)m.invoke(null,results);
                //killConnection(connection);
                return ret;
            } catch (Exception e) {
                try {
                // try it with Table
                Class[] argTypes = new Class[] {ResultSet.class,Logger.class};
                Method m = Table.class.getDeclaredMethod("parseResult",argTypes);
                ret = (List<? extends Table>)m.invoke(null,results,getLogger());
                //killConnection(connection);
                return ret;
                } catch (Exception e2) {
                    getLogger().warning("select: Unable to invoke parseResult for " + c.getName());
                    e2.printStackTrace();
                    //killConnection(connection);
                    return ret;
                }
            }
        } catch (Exception e) {
            getLogger().warning("select: Unable to run query:");
            getLogger().warning(query);
            getLogger().warning(e.getMessage());
            e.printStackTrace();
            getLogger().warning("Closing connection.");
            killConnection(connection);
            return ret;
        }

    }

    private void killConnection(Connection c) {
        try {
            c.close();
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

        try {
            //if (connection.isClosed()) {
                //getLogger().warning("Connection closed, attempting reconnect");
                killConnection(connection);
                connection = DriverManager.getConnection(url,username,password);
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
            //killConnection(connection);
        } catch (Exception e) {
            getLogger().warning("query: Unable to run query:");
            getLogger().warning(query);
            getLogger().info(e.getMessage());
            e.printStackTrace();
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
}
