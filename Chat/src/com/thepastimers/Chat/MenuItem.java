package com.thepastimers.Chat;

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

    public String getJson(int count) {
        String ret = "{text:\"";
        if (parent != null && parent.useNumbers) {
            ret += count;
        }
        ret += text + "\"";
        if (action != null && !action.equals("")) {
            if (action.equalsIgnoreCase("menu")) {
                ret += " clickEvent:{action:run_command,value:\"/menu " + data + "\"}";
            } else if (action.equalsIgnoreCase("command")) {
                ret += " clickEvent:{action:run_command,value:\"/" + data + "\"}";
            } else if (action.equalsIgnoreCase("suggest")) {
                ret += " clickEvent:{action:suggest_command,value:\"/" + data + "\"}";
            }
        }
        if (hover != null && !hover.equalsIgnoreCase("")) {
            ret += " hoverEvent:{action:show_text,value:\"" + hover + "\"}";
        }

        ret += "}";

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
