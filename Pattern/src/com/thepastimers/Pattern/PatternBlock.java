package com.thepastimers.Pattern;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/5/13
 * Time: 12:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class PatternBlock extends Table {
    public static String table = "pattern_block";
    
    int id;
    
    public PatternBlock() {
        id = -1;
    }
    
    String pattern;
    int x;
    int y;
    int z;
    String block;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public static List<PatternBlock> parseResult(ResultSet result) throws SQLException {
        List<PatternBlock> ret = new ArrayList<PatternBlock>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            PatternBlock p = new PatternBlock();

            p.setId(result.getInt("id"));
            p.setPattern(result.getString("pattern"));
            p.setX(result.getInt("x"));
            p.setY(result.getInt("y"));
            p.setZ(result.getInt("z"));
            p.setBlock(result.getString("block"));

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
            String columns = "(pattern,x,y,z,block)";
            String values = "('" + d.makeSafe(pattern) + "'," + x + "," + y + "," + z + ",'" + d.makeSafe(block) + "')";
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

            query.append("player = '" + d.makeSafe(pattern) + "', ");
            query.append("x = " + z + ", ");
            query.append("y = " + y + ", ");
            query.append("z = " + z + ", ");
            query.append("block = '" + d.makeSafe(block) + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string pattern, int x, int y, int z, string block");

        return builder.toString();
    }
}
