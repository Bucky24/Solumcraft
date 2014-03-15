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
 * User: rwijtman
 * Date: 8/13/13
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class RankData extends Table {
    public static String table = "rank_data";

    private static Map<Integer,RankData> dataMap;
    
    int id;
    
    public RankData() {
        id = -1;
    }
    
    String rank;
    String parentRank;
    String code;
    String format;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
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
            r.setCode(result.getString("code"));
            r.setFormat(result.getString("format"));

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
            String columns = "(parent_rank,rank,code,format)";
            String values = "('" + d.makeSafe(parentRank) + "','" + d.makeSafe(rank)  + "','" + d.makeSafe(code) + "','" + d.makeSafe(format) + "')";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("parent_rank = '" + d.makeSafe(parentRank) + "'" + ", ");
            query.append("rank = '" + d.makeSafe(rank) + "', ");
            query.append("code = '" + d.makeSafe(code) + "', ");
            query.append("format = '" + d.makeSafe(format) + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);
        builder.append(" int id, string parent_rank, string rank, string code, string format");

        return builder.toString();
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<RankData> RankDataList = (List<RankData>)d.select(RankData.class,"1");

        dataMap = new HashMap<Integer, RankData>();

        if (RankDataList == null) {
            return;
        }

        for (RankData rd : RankDataList) {
            dataMap.put(rd.getId(),rd);
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    public static RankData getDataForRank(String rank) {
        for (Integer i : dataMap.keySet()) {
            RankData r = dataMap.get(i);
            if (r.getRank().equalsIgnoreCase(rank)) {
                return r;
            }
        }

        return null;
    }

    public static List<RankData> getRankChain(String rank) {
        List<RankData> ranks = new ArrayList<RankData>();

        RankData rd = RankData.getDataForRank(rank);
        while (rd != null) {
            ranks.add(rd);
            rd = RankData.getDataForRank(rd.getParentRank());
        }

        return ranks;
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        StringBuilder definition = new StringBuilder("CREATE TABLE " + table + "(");
        definition.append("`id` int(11) NOT NULL AUTO_INCREMENT,");

        definition.append("`parent_rank` varchar(100) NOT NULL,");
        definition.append("`rank` varchar(100) NOT NULL,");
        definition.append("`code` varchar(20) NOT NULL,");
        definition.append("`format` varchar(100) NOT NULL,");

        definition.append("PRIMARY KEY (`id`)");
        definition.append(");");
        boolean result = d.createTableIfNotExists(table,definition.toString());

        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }
}
