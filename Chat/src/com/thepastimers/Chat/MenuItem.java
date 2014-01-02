package com.thepastimers.Chat;

import org.json.simple.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 12/2/13
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class MenuItem {
    Menu parent;
    String text;
    String action;
    String data;
    String hover;

    public MenuItem(String text) {
        this.text = text;
        this.action = null;
        this.data = null;
        this.hover = null;
    }

    public MenuItem(String text, String hover) {
        this.text = text;
        this.action = null;
        this.data = null;
        this.hover = hover;
    }

    public MenuItem(String text,String action,String data) {
        this.text = text;
        this.action = action;
        this.data = data;
        this.hover = null;
    }

    public MenuItem(String text,String action,String data,String hover) {
        this.text = text;
        this.action = action;
        this.data = data;
        this.hover = hover;
    }

    public JSONObject getJson(int count) {
        JSONObject ret = new JSONObject();
        if (parent != null && parent.useNumbers) {
            ret.put("text",count + ": " + text);
        } else {
            ret.put("text",text);
        }
        if (action != null && !action.equals("")) {
            JSONObject a = new JSONObject();
            if (action.equalsIgnoreCase("menu")) {
                a.put("action","run_command");
                a.put("value","/menu " + data);
            } else if (action.equalsIgnoreCase("command")) {
                a.put("action","run_command");
                a.put("value","/" + data);
            } else if (action.equalsIgnoreCase("suggest")) {
                a.put("action","suggest_command");
                a.put("value","/" + data);
            }
            if (a.keySet().size() > 0) {
                ret.put("clickEvent",a);
            }
        }

        return ret;
    }

    public Menu getParent() {
        return parent;
    }

    public void setParent(Menu parent) {
        this.parent = parent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
