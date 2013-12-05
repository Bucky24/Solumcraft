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
 * User: rwijtman
 * Date: 8/13/13
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class RankData extends Table {
    public static String table = "rank_data";
    
    int id;
    
    public RankData() {
        id = -1;
    }
    
    String rank;
    String parentRank;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getParentRank() {
        return parentRank;
    }

    public void setParentRank(String parentRank) {
        this.parentRank = parentRank;
    }

    public static List<RankData> parseResult(ResultSet result) throws SQLException {
        List<RankData> ret = new ArrayList<RankData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            RankData r = new RankData();
            r.setId(result.getInt("id"));
            r.setParentRank(result.getString("parent_rank"));
            r.setRank(result.getString("rank"));

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
        return d.query("DELETE FROM " + table + " WHERE id = " + id);
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(parent_rank,rank)";
            String values = "('" + d.makeSafe(parentRank) + "','" + d.makeSafe(rank)  + "')";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("parent_rank = '" + d.makeSafe(parentRank) + "'" + ", ");
            query.append("rank = '" + d.makeSafe(rank) + "'");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);
        builder.append(" int id, string parent_rank, string rank");

        return builder.toString();
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        StringBuilder definition = new StringBuilder("CREATE TABLE " + table + "(");
        definition.append("`id` int(11) NOT NULL AUTO_INCREMENT,");

        definition.append("`parent_rank` varchar(100) NOT NULL,");
        definition.append("`rank` varchar(100) NOT NULL,");

        definition.append("PRIMARY KEY (`id`)");
        definition.append(");");
        boolean result = d.createTableIfNotExists(table,definition.toString());

        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }
}
