package com.thepastimers.Database;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
    private static Map<Class,Map<String,String>> columns = new HashMap<Class,Map<String, String>>();

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
        return parseResult(resultSet,null);
    }

    public static List<? extends Table> parseResult(ResultSet resultSet,Class myClass) throws Exception {
        List<Table> ret = new ArrayList<Table>();

        ResultSetMetaData data = resultSet.getMetaData();

        Map<String,String> newCols = columns.get(myClass);
        if (newCols == null) {
            newCols = new HashMap<String, String>();
        }

        if (autoPopulate) {
            int count = data.getColumnCount();
            for (int i=1;i<=count;i++) {
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

                if (!newCols.containsKey(name)) {
                    newCols.put(name,typeStr);
                }
            }

            columns.put(myClass,newCols);

            while (resultSet.next()) {
                Table instance = (Table)myClass.newInstance();
                for (int i=1;i<=data.getColumnCount();i++) {
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

                    try {
                        Method m = myClass.getDeclaredMethod(function,argTypes);
                        m.invoke(instance,value);
                    } catch (Exception e) {
                        // ignore
                    }
                }
                ret.add(instance);
            }
        }

        return ret;
    }

    public boolean save(Database d, Class myClass) {
        if (d == null) {
            return false;
        }
        if (autoPopulate) {
            try {
                table = (String)myClass.getField("table").get(null);
            } catch (Exception e) {
                // can't do anything
            }

            Map<String,String> colMap = columns.get(myClass);

            // get column count
            String[] keys = colMap.keySet().toArray(new String[0]);

            if (id == -1) {
                StringBuilder cols = new StringBuilder();
                cols.append("(");
                StringBuilder values = new StringBuilder();
                values.append("(");

                for (int i=0;i<keys.length;i++) {
                    String name = keys[i];
                    String type = colMap.get(name);

                    //d.getLogger().info(name);

                    // don't save the ID
                    if ("id".equalsIgnoreCase(name)) {
                        continue;
                    }

                    char[] stringArray = name.toCharArray();
                    stringArray[0] = Character.toUpperCase(stringArray[0]);
                    String function = new String(stringArray);
                    function = "get" + function;

                    Object value;
                    try {
                        Class[] argTypes = new Class[0];
                        Method m = myClass.getDeclaredMethod(function,argTypes);
                        value = m.invoke(this);
                    } catch (Exception e) {
                        // ignore
                        continue;
                    }

                    cols.append(name);
                    if ("string".equalsIgnoreCase(type) || "timestamp".equalsIgnoreCase(type)) {
                        values.append("\"").append(value).append("\"");
                    } else {
                        values.append(value);
                    }

                    if (i < keys.length-1) {
                        //d.getLogger().info(i + " < " + (max-1));
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

                for (int i=0;i<keys.length;i++) {
                    String name = keys[i];
                    String type = colMap.get(name);

                    // don't save the id (though in an update it doesn't really matter
                    if ("id".equalsIgnoreCase(name)) {
                        continue;
                    }

                    char[] stringArray = name.toCharArray();
                    stringArray[0] = Character.toUpperCase(stringArray[0]);
                    String function = new String(stringArray);
                    function = "get" + function;

                    Object value;
                    try {
                        Class[] argTypes = new Class[0];
                        Method m = myClass.getDeclaredMethod(function,argTypes);
                        value = m.invoke(this);
                    } catch (Exception e) {
                        return false;
                    }

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

    protected static Class getChildClass() {
        try {
            // hopefully returns the name of the child class
            String classname = (new CurClassNameGetter()).getClassName();
            Class c = Class.forName(classname);
            return c;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getTableInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(table);
        /*if (autoPopulate) {
            Class myClass = getChildClass();
            if (myClass == null) return sb.toString();
            Map<String,String> colMap = columns.get(myClass);
            sb.append(":");
            for (String key : colMap.keySet()) {
                sb.append(" ").append(colMap.get(key)).append(" ").append(key).append(",");
            }
        }*/
        return sb.toString();
    }

    public static String getTableInfo(boolean blah) {
        StringBuilder sb = new StringBuilder();
        sb.append(table);
        if (autoPopulate) {
            Class myClass = getChildClass();
            if (myClass == null) return sb.toString();
            Map<String,String> colMap = columns.get(myClass);
            sb.append(":");
            for (String key : colMap.keySet()) {
                sb.append(" ").append(colMap.get(key)).append(" ").append(key).append(",");
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
        if (autoPopulate) {
            Class myClass = getChildClass();
            try {
                table = (String)myClass.getField("table").get(null);
            } catch (Exception e) {
                // can't do anything
            }
        }
        return d.query("DELETE FROM " + table + " WHERE ID = " + id);
    }

    private static class CurClassNameGetter extends SecurityManager{
        public String getClassName(){
            return getClassContext()[1].getName();
        }
    }
}
