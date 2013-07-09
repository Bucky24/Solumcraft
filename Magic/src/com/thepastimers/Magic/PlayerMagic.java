package com.thepastimers.Magic;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 2/25/13
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerMagic extends Table {
    public static String table = "player_magic";

    int id;

    public PlayerMagic() {
        id = -1;
    }

    String player;
    String wandSpell;
    double maxMana;

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

    public String getWandSpell() {
        return wandSpell;
    }

    public void setWandSpell(String wandSpell) {
        this.wandSpell = wandSpell;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
    }

    public static List<PlayerMagic> parseResult(ResultSet result) throws SQLException {
        List<PlayerMagic> ret = new ArrayList<PlayerMagic>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            PlayerMagic p = new PlayerMagic();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setMaxMana(result.getDouble("maxMana"));

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
            String columns = "(player,maxMana)";
            String values = "('" + d.makeSafe(player) + "'," + maxMana + ")";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("player = '" + d.makeSafe(player) + "'" + ", ");
            query.append("maxMana = " + maxMana + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, double maxMana");

        return builder.toString();
    }
}
