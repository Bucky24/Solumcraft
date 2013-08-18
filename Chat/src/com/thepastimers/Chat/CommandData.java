package com.thepastimers.Chat;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/22/13
 * Time: 9:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommandData extends Table {
    public static String table = "commands";

    int id;

    public CommandData() {
        id = -1;
    }

    String player;
    String command;
    String arguments;
    Timestamp time;
    boolean handled;
    String response;
    boolean read;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getArguments() {
        return arguments;
    }

    public String[] getArgumentArray() {
        return arguments.split(" ");
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public static List<CommandData> parseResult(ResultSet result) throws SQLException {
        List<CommandData> ret = new ArrayList<CommandData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            CommandData p = new CommandData();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setCommand(result.getString("command"));
            p.setArguments(result.getString("arguments"));
            p.setTime(result.getTimestamp("time"));
            p.setHandled(result.getBoolean("handled"));
            p.setResponse(result.getString("response"));
            p.setRead(result.getBoolean("read"));

            ret.add(p);
        }

        return ret;
    }

    public boolean delete(Database d) {
        if (id == -1) {
            return true;
        }
        if (d == null) {
            return false;
        }
        return d.query("DELETE FROM " + table + " WHERE ID = " + id);
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(player,command,arguments,time,handled,response,`read`)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(command) + "','" + d.makeSafe(arguments)
                    + "','" + time + "'," + handled + ",'" + d.makeSafe(response) + "'," + read + ")";
            boolean result = d.query("INSERT INTO " + table + columns + " VALUES" + values);

            ResultSet keys = d.getGeneratedKeys();

            if (keys != null && result) {
                try {
                    if (keys.next()) {
                        setId(keys.getInt(1));
                    }
                } catch (SQLException e) {
                    // fallback method
                }
            }

            return result;
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("player = '" + d.makeSafe(player) + "', ");
            query.append("command = '" + d.makeSafe(command) + "', ");
            query.append("arguments = '" + d.makeSafe(arguments) + "', ");
            query.append("time = '" + time + "', ");
            query.append("handled = " + handled + ", ");
            query.append("response = '" + d.makeSafe(response) + "', ");
            query.append("`read` = " + read + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, string command, string arguments, timestamp time, boolean handled, string response, boolean read");

        return builder.toString();
    }
}
