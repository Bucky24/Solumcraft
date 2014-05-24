package com.thepastimers.Chat;

import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/24/14
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChatObject {
    JSONArray array;

    public ChatObject() {
        array = new JSONArray();
    }

    public ChatObject text(String text) {
        return text(text,ChatColor.WHITE);
    }

    public ChatObject text(String text, ChatColor color) {
        JSONObject obj = new JSONObject();
        obj.put("text",text);
        obj.put("color",color.name().toLowerCase());
        array.add(obj);
        return this;
    }

    public ChatObject url(String url) {
        return url(url,url,ChatColor.WHITE);
    }

    public ChatObject url(String text, String url) {
        return url(text,url,ChatColor.WHITE);
    }

    public ChatObject url(String url, ChatColor color) {
        return url(url,url,color);
    }

    public ChatObject url(String text, String url, ChatColor color) {
        JSONObject obj = new JSONObject();
        obj.put("text",text);
        JSONObject urlObj = new JSONObject();
        urlObj.put("action","open_url");
        urlObj.put("value",url);
        obj.put("clickEvent",urlObj);
        obj.put("color",color.name().toLowerCase());
        array.add(obj);
        return this;
    }

    public ChatObject command(String text, String command) {
        return command(text,command,ChatColor.WHITE);
    }

    public ChatObject command(String text, String command, ChatColor color) {
        JSONObject obj = new JSONObject();
        obj.put("text",text);
        JSONObject urlObj = new JSONObject();
        urlObj.put("action","run_command");
        urlObj.put("value",command);
        obj.put("clickEvent",urlObj);
        obj.put("color",color.name().toLowerCase());
        array.add(obj);
        return this;
    }

    public String toString() {
        return array.toString();
    }
}
