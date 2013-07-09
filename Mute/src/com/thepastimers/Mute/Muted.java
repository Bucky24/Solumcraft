package com.thepastimers.Mute;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/2/12
 * Time: 8:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class Muted extends Table {
    public static String table = "muted";

    int id;

    public Muted() {
        id = -1;
    }

    String muted;
    String mutee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMuted() {
        return muted;
    }

    public void setMuted(String muted) {
        this.muted = muted;
    }

    public String getMutee() {
        return mutee;
    }

    public void setMutee(String mutee) {
        this.mutee = mutee;
    }

    public static List<Muted> parseResult(ResultSet result) throws SQLException {
        List<Muted> ret = new ArrayList<Muted>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            Muted m = new Muted();
            m.setId(result.getInt("id"));
            m.setMuted(result.getString("muted"));
            m.setMutee(result.getString("mutee"));

            ret.add(m);
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
            String columns = "(muted,mutee)";
            String values = "('" + d.makeSafe(muted) + "','" + d.makeSafe(mutee)  + "')";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("muted = '" + d.makeSafe(muted) + "'" + ", ");
            query.append("mutee = '" + d.makeSafe(mutee) + "'");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }
}
