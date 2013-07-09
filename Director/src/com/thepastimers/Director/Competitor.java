package com.thepastimers.Director;

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
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Competitor extends Table {
    public static String table = "arena_player";

    int id;

    public Competitor() {
        id = -1;
    }

    String player;
    int arena;
    int points;
    int deaths;
    int kills;
    double prevX;
    double prevY;
    double prevZ;
    String prevWorld;
    boolean active;

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

    public int getArena() {
        return arena;
    }

    public void setArena(int arena) {
        this.arena = arena;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public double getPrevX() {
        return prevX;
    }

    public void setPrevX(double prevX) {
        this.prevX = prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public void setPrevY(double prevY) {
        this.prevY = prevY;
    }

    public double getPrevZ() {
        return prevZ;
    }

    public void setPrevZ(double prevZ) {
        this.prevZ = prevZ;
    }

    public String getPrevWorld() {
        return prevWorld;
    }

    public void setPrevWorld(String prevWorld) {
        this.prevWorld = prevWorld;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static List<Competitor> parseResult(ResultSet result) throws SQLException {
        List<Competitor> ret = new ArrayList<Competitor>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            Competitor p = new Competitor();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setArena(result.getInt("arena"));
            p.setPoints(result.getInt("points"));
            p.setDeaths(result.getInt("deaths"));
            p.setKills(result.getInt("kills"));
            p.setPrevX(result.getDouble("prev_x"));
            p.setPrevY(result.getDouble("prev_y"));
            p.setPrevZ(result.getDouble("prev_z"));
            p.setPrevWorld(result.getString("prev_world"));
            p.setActive(result.getBoolean("active"));

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
            String columns = "(player,arena,points,deaths,kills,prev_x,prev_y,prev_z,prev_world,active)";
            String values = "('" + d.makeSafe(player) + "'," + arena + "," + points + "," + deaths + "," + kills + "," +
                    prevX + "," + prevY + "," + prevZ + ",'" + d.makeSafe(prevWorld) + "'," + active + ")";
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
            query.append("arena = " + arena + ", ");
            query.append("points = " + points + ", ");
            query.append("deaths = " + deaths + ", ");
            query.append("kills = " + kills + ", ");
            query.append("prev_x = " + prevX + ", ");
            query.append("prev_y = " + prevY + ", ");
            query.append("prev_z = " + prevZ + ", ");
            query.append("prev_world = '" + d.makeSafe(prevWorld) + "', ");
            query.append("active = " + active + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, int arena, int points, int deaths, int kills, int prev_x, int prev_y, ");
        builder.append("int prev_z, string prev_world, boolean active");

        return builder.toString();
    }
}
