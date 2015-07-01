package org.bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solum on 6/30/2015.
 */
public class Text {
    private List<TextElement> elements;

    public Text() {
        elements = new ArrayList<TextElement>();
    }

    public static Text make() {
        return new Text();
    }

    public Text text(String message) {
        elements.add(new TextElement(TextElement.TEXT,message));
        return this;
    }

    public Text color(ChatColor color) {
        elements.add(new TextElement(TextElement.COLOR,color));
        return this;
    }
}

class TextElement {
    public static int TEXT = 1;
    public static int COLOR = 2;

    public int type;
    public Object data;

    public TextElement(int type, Object data) {
        this.type = type;
        this.data = data;
    }
}
