package com.thepastimers.Rank;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        StringBuilder definition = new StringBuilder("CREATE TABLE " + table + "(");
        definition.append("`id` int(11) NOT NULL AUTO_INCREMENT,");

        definition.append("`player` varchar(50) NOT NULL,");
        definition.append("`title` varchar(100) NOT NULL,");

        definition.append("PRIMARY KEY (`id`)");
        definition.append(");");
        boolean result = d.createTableIfNotExists(table,definition.toString());

        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }
}
