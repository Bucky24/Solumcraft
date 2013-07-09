package com.thepastimers.AdminTools;

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
 * Date: 4/22/13
 * Time: 6:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class BanData extends Table {
    public static String table = "ban_data";
    
    int id;
    
    public BanData() {
        id = -1;
        active = true;
    }
    
    String player;
    String reason;
    Timestamp entered;
    Timestamp until;
    boolean active;
    boolean perm;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getEntered() {
        return entered;
    }

    public void setEntered(Timestamp entered) {
        this.entered = entered;
    }

    public Timestamp getUntil() {
        return until;
    }

    public void setUntil(Timestamp until) {
        this.until = until;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPerm() {
        return perm;
    }

    public void setPerm(boolean perm) {
        this.perm = perm;
    }

    public static List<BanData> parseResult(ResultSet result) throws SQLException {
        List<BanData> ret = new ArrayList<BanData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            BanData p = new BanData();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setReason(result.getString("reason"));
            p.setEntered(result.getTimestamp("entered"));
            p.setUntil(result.getTimestamp("until"));
            p.setActive(result.getBoolean("active"));
            p.setPerm(result.getBoolean("perm"));

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
        String enterString = "null";
        if (entered != null) enterString = "'" + entered + "'";
        String untilString = "null";
        if (until != null) untilString = "'" + until + "'";
        if (id == -1) {
            String columns = "(player,reason,entered,until,active,perm)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(reason) + "'," + enterString + ","
                    + untilString + "," + active + "," + perm + ")";
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
            query.append("reason = '" + d.makeSafe(reason) + "', ");
            query.append("entered = " + enterString + ", ");
            query.append("until = " + untilString + ", ");
            query.append("active = " + active + ", ");
            query.append("perm = " + perm + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, string reason, timestamp entered, timestamp until, boolean active, boolean perm");

        return builder.toString();
    }
}
