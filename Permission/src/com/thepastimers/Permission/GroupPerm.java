package com.thepastimers.Permission;

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
 * User: derp
 * Date: 10/1/12
 * Time: 8:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroupPerm extends Table {
    public static String table = "group_permissions";

    int id;

    private static Map<Integer,GroupPerm> dataMap;

    public GroupPerm() {
        id = -1;
    }

    String group;
    String permission;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public static List<GroupPerm> parseResult(ResultSet result) throws SQLException {
        List<GroupPerm> ret = new ArrayList<GroupPerm>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            GroupPerm g = new GroupPerm();

            g.setId(result.getInt("id"));
            g.setGroup(result.getString("group"));
            g.setPermission(result.getString("permission"));

            ret.add(g);
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
        boolean result = d.query("DELETE FROM " + table + " WHERE ID = " + id);

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
            String columns = "(`group`,permission)";
            String values = "('" + d.makeSafe(group) + "','" + d.makeSafe(permission)  + "')";
            boolean result = d.query("INSERT INTO " + table + columns + " VALUES" + values);

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

            query.append("`group` = '" + d.makeSafe(group) + "'" + ", ");
            query.append("permission = '" + d.makeSafe(permission) + "'");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        List<GroupPerm> GroupPermList = (List<GroupPerm>)d.select(GroupPerm.class,"");

        dataMap = new HashMap<Integer, GroupPerm>();

        if (GroupPermList == null) {
            return;
        }

        for (GroupPerm pd : GroupPermList) {
            dataMap.put(pd.getId(),pd);
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    public static boolean hasPermission(String group, String permission) {
        if (group == null || permission == null) {
            return false;
        }

        for (Integer id : dataMap.keySet()) {
            GroupPerm perm = dataMap.get(id);

            if (group.equals(perm.getGroup()) && permission.equalsIgnoreCase(perm.getPermission())) {
                return true;
            }
        }

        return false;
    }
}
