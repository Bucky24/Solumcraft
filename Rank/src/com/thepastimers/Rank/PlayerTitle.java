package com.thepastimers.Rank;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 2/23/13
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerTitle extends Table {
    public static String table = "title";

    private static Map<Integer,PlayerTitle> dataMap;

    String player;
    String title;

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static List<PlayerTitle> parseResult(ResultSet result) throws SQLException {
        List<PlayerTitle> ret = new ArrayList<PlayerTitle>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            PlayerTitle r = new PlayerTitle();
            r.setId(result.getInt("id"));
            r.setPlayer(result.getString("player"));
            r.setTitle(result.getString("title"));

            ret.add(r);
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
        boolean result = d.query("DELETE FROM " + table + " WHERE id = " + id);
        if (result) {
            dataMap.remove(id);
        }
        return result;
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(player,title)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(title)  + "')";
            String query = "INSERT INTO " + table + columns + " VALUES" + values;
            boolean result = d.query(query);


            ResultSet keys = d.getGeneratedKeys();

            if (keys != null && result) {
                try {
                    if (keys.next()) {
                        setId(keys.getInt(1));
                        dataMap.put(getId(),this);
                    }
                } catch (SQLException e) {
                    // fallback method
                    refreshCache(d,null);
                }
            }
            return result;
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("player = '" + d.makeSafe(player) + "'" + ", ");
            query.append("title = '" + d.makeSafe(title) + "'");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);
        builder.append(" int id, string player, string rank");

        return builder.toString();
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<PlayerTitle> PlayerTitleList = (List<PlayerTitle>)d.select(PlayerTitle.class,"1");
        //l.info(PlayerTitleList.size() + "");

        dataMap = new HashMap<Integer, PlayerTitle>();

        if (PlayerTitleList == null) {
            return;
        }

        for (PlayerTitle pd : PlayerTitleList) {
           // l.info(pd.getId() + "");
            dataMap.put(pd.getId(),pd);
            //l.info(dataMap.size() + "");
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    public static PlayerTitle getTitle(String player) {
        for (Integer i : dataMap.keySet()) {
            PlayerTitle r = dataMap.get(i);
            if (r.getPlayer().equalsIgnoreCase(player)) {
                return r;
            }
        }

        return null;
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        StringBuilder definition = new StringBuilder("CREATE TABLE " + table + "(");
        definition.append("id int(11) NOT NULL AUTO_INCREMENT,");

        definition.append("player varchar(50) NOT NULL,");
        definition.append("title varchar(100) NOT NULL,");

        definition.append("PRIMARY KEY (id)");
        definition.append(");");
        boolean result = d.createTableIfNotExists(table,definition.toString());

        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }
}
