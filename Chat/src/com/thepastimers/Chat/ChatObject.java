package com.thepastimers.Chat;

/*
 * Note: this requires the json-simple jar to be imported to the project and added to the server to run.
 * It was easy enough to find online this time, hopefully will be next time I run into this again.
 */

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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

    public static ChatObject make() {
        return new ChatObject();
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

    public ChatObject suggest(String text, String command) {
        return suggest(text,command,ChatColor.WHITE);
    }

    public ChatObject suggest(String text, String command, ChatColor color) {
        JSONObject obj = new JSONObject();
        obj.put("text",text);
        JSONObject urlObj = new JSONObject();
        urlObj.put("action","suggest_command");
        urlObj.put("value",command);
        obj.put("clickEvent",urlObj);
        obj.put("color",color.name().toLowerCase());
        array.add(obj);
        return this;
    }

    public String getText() {
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<array.size();i++) {
            JSONObject obj = (JSONObject)array.get(i);
            if (obj.containsKey("text")) {
                builder.append(obj.get("text"));
            }
        }

        return builder.toString();
    }

    public void send(Chat c, Player p) {
        c.sendRaw(this,p);
    }

    public String toString() {
        return array.toString();
    }
}
