package com.thepastimers.Home;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/2/12
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MaxHome extends Table {
    public static String table = "max_home";

    int id;

    public MaxHome() {
        id = -1;
    }

    String name;
    int max;
    boolean group;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public static List<MaxHome> parseResult(ResultSet result) throws SQLException {
        List<MaxHome> ret = new ArrayList<MaxHome>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            MaxHome p = new MaxHome();

            p.setId(result.getInt("id"));
            p.setName(result.getString("name"));
            p.setMax(result.getInt("max"));
            p.setGroup(result.getBoolean("group"));

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
            String columns = "(name,max,`group`)";
            String values = "('" + d.makeSafe(name) + "'," + max + "," + group + ")";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("name = '" + d.makeSafe(name) + "'" + ", ");
            query.append("max = " + max + ", ");
            query.append("`group` = " + group + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string name, int max, bool group");

        return builder.toString();
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        StringBuilder definition = new StringBuilder("CREATE TABLE " + table + "(");
        definition.append("`id` int(11) NOT NULL AUTO_INCREMENT,");

        definition.append("`name` varchar(50) NOT NULL,");
        definition.append("`max` int(11) NOT NULL,");
        definition.append("`group` int(1) NOT NULL,");

        definition.append("PRIMARY KEY (`id`)");
        definition.append(");");
        boolean result = d.createTableIfNotExists(table,definition.toString());

        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }
}
