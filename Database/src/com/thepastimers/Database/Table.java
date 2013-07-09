package com.thepastimers.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/1/12
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Table {
    public static String table = "";

    int id;

    public Table() {
        id = -1;
    }

    public static List<? extends Table> parseResult(ResultSet resultSet) throws SQLException {
        List<Table> ret = new ArrayList<Table>();

        return ret;
    }

    public static String getTableInfo() {
        return "";
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
}
