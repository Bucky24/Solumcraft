package com.thepastimers.Chat;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;
import BukkitBridge.Text;

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
public class ChatData extends Table {
    public static String table = "chat";
    
    int id;
    
    public ChatData() {
        id = -1;
    }
    
    String player;
    String message;
    Timestamp time;
    boolean seen;
    Text playerString;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Text getPlayerString() {
        return playerString;
    }

    public void setPlayerString(String playerString) {
        this.playerString = Text.make().text(playerString);
    }

    public void setPlayerString(Text playerString) {
        this.playerString = playerString;
    }

    public static List<ChatData> parseResult(ResultSet result) throws SQLException {
        List<ChatData> ret = new ArrayList<ChatData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            ChatData p = new ChatData();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setMessage(result.getString("message"));
            p.setTime(result.getTimestamp("time"));
            p.setSeen(result.getBoolean("seen"));
            p.setPlayerString(result.getString("player_string"));

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
            String columns = "(player,message,time,seen,player_string)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(message) + "','" + time + "'," + seen + ",'" + d.makeSafe(playerString.toString()) + "')";
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
            query.append("message = '" + d.makeSafe(message) + "', ");
            query.append("time = '" + time + "', ");
            query.append("seen = " + seen + ", ");
            query.append("player_string = '" + d.makeSafe(playerString.toString()) + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, string message, timestamp time, boolean seen, string player_string");

        return builder.toString();
    }
}
