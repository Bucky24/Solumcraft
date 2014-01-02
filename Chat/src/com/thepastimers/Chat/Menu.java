package com.thepastimers.Chat;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 12/2/13
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class Menu {
    List<MenuItem> items;
    String header;
    boolean useNumbers = false;

    public Menu(String header) {
        this.header = header;
        items = new ArrayList<MenuItem>();
    }

    public void addItem(MenuItem item) {
        item.setParent(this);
        items.add(item);
    }

    public void sendMenuTo(Player p, Chat chat) {
        p.sendMessage(header);
        for (int i=0;i<items.size();i++) {
            chat.sendRaw(items.get(i).getJson(i),p);
        }
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean isUseNumbers() {
        return useNumbers;
    }

    public void setUseNumbers(boolean useNumbers) {
        this.useNumbers = useNumbers;
    }
}
