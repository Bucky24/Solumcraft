package com.thepastimers.Database;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/1/12
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Table {
    public static String table = "";

    protected int id;

    public static boolean autoPopulate = false;
    public static Class myClass = null;
    private static Map<String,String> columns = new HashMap<String, String>();

    public Table() {
        id = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static List<? extends Table> parseResult(ResultSet resultSet) throws Exception {
        List<Table> ret = new ArrayList<Table>();

        ResultSetMetaData data = resultSet.getMetaData();

        if (autoPopulate) {
            for (int i=0;i<data.getColumnCount();i++) {
                int type = data.getColumnType(i);
                String name = data.getColumnName(i);

                String typeStr;
                if (type == Types.VARCHAR) {
                    typeStr = "string";
                } else if (type == Types.INTEGER) {
                    typeStr = "int";
                } else if (type == Types.TIMESTAMP) {
                    typeStr = "timestamp";
                } else if (type == Types.BOOLEAN) {
                    typeStr = "bool";
                } else {
                    throw new UnsupportedOperationException("Can't handle columns of type " + type);
                }

                if (!columns.containsKey(name)) {
                    columns.put(name,typeStr);
                }
            }

            while (resultSet.next()) {
                Table instance = (Table)myClass.newInstance();
                for (int i=0;i<data.getColumnCount();i++) {
                    int type = data.getColumnType(i);
                    String name = data.getColumnName(i);

                    Class[] argTypes;
                    Object value;
                    if (type == Types.VARCHAR) {
                        argTypes = new Class[] {String.class};
                        value = resultSet.getString(i);
                    } else if (type == Types.INTEGER) {
                        argTypes = new Class[] {Integer.class};
                        value = resultSet.getInt(i);
                    } else if (type == Types.TIMESTAMP) {
                        argTypes = new Class[] {Timestamp.class};
                        value = resultSet.getTimestamp(i);
                    } else if (type == Types.BOOLEAN) {
                        argTypes = new Class[] {Boolean.class};
                        value = resultSet.getBoolean(i);
                    } else {
                        throw new UnsupportedOperationException("Can't handle columns of type " + type);
                    }

                    char[] stringArray = name.toCharArray();
                    stringArray[0] = Character.toUpperCase(stringArray[0]);
                    String function = new String(stringArray);
                    function = "set" + function;

                    Method m = myClass.getDeclaredMethod(function,argTypes);
                    m.invoke(instance,value);
                }
            }
        }

        return ret;
    }

    public boolean save(Database d) throws Exception {
        if (d == null) {
            return false;
        }
        if (autoPopulate) {
            if (id == -1) {
                StringBuilder cols = new StringBuilder();
                cols.append("(");
                StringBuilder values = new StringBuilder();
                values.append("(");

                String[] keys = (String[])columns.keySet().toArray();
                for (int i=0;i<keys.length;i++) {
                    String name = keys[i];
                    String type = columns.get(name);

                    char[] stringArray = name.toCharArray();
                    stringArray[0] = Character.toUpperCase(stringArray[0]);
                    String function = new String(stringArray);
                    function = "get" + function;

                    Class[] argTypes = new Class[0];
                    Method m = myClass.getDeclaredMethod(function,argTypes);
                    Object value = m.invoke(this);

                    cols.append(name);
                    if ("string".equalsIgnoreCase(type) || "timestamp".equalsIgnoreCase(type)) {
                        values.append("\"").append(value).append("\"");
                    } else {
                        values.append(value);
                    }

                    if (i < keys.length-1) {
                        cols.append(",");
                        values.append(",");
                    }
                }

                cols.append(")");
                values.append(")");

                boolean result = d.query("INSERT INTO " + table + cols + " VALUES" + values);

                return result;
            } else {
                StringBuilder query = new StringBuilder();
                query.append("UPDATE " + table + " SET ");

                String[] keys = (String[])columns.keySet().toArray();
                for (int i=0;i<keys.length;i++) {
                    String name = keys[i];
                    String type = columns.get(name);

                    char[] stringArray = name.toCharArray();
                    stringArray[0] = Character.toUpperCase(stringArray[0]);
                    String function = new String(stringArray);
                    function = "get" + function;

                    Class[] argTypes = new Class[0];
                    Method m = myClass.getDeclaredMethod(function,argTypes);
                    Object value = m.invoke(this);

                    query.append(name).append("=");
                    if ("string".equalsIgnoreCase(type) || "timestamp".equalsIgnoreCase(type)) {
                        query.append("\"").append(value).append("\"");
                    } else {
                        query.append(value);
                    }

                    if (i < keys.length-1) {
                        query.append(",");
                    }
                }

                query.append(" WHERE id = " + id);
                return d.query(query.toString());
            }
        } else {
            return false;
        }
    }

    public static String getTableInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(table);
        if (autoPopulate) {
            sb.append(":");
            for (String key : columns.keySet()) {
                sb.append(" ").append(columns.get(key)).append(" ").append(key).append(",");
            }
        }
        return sb.toString();
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
