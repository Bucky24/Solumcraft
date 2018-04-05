package org.bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solum on 6/30/2015.
 */
public class Text {
    public static class TextElement {
        public static int TEXT = 1;
        public static int COLOR = 2;
        public static int STYLE = 3;
        public static int COMPOUND = 4;
        public static int COMMAND = 5;
        public static int SUGGEST = 6;

        public int type;
        public Object data;
        // Used for command string (since data is the display message)
        public Object data2;

        public TextElement(int type, Object data) {
            this.type = type;
            this.data = data;
        }

        public TextElement(int type, Object data, Object data2) {
            this.type = type;
            this.data = data;
            this.data2 = data2;
        }
    }

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

    public Text style(ChatStyle style) {
        elements.add(new TextElement(TextElement.STYLE, style));
        return this;
    }

    public Text compound(Text otherText) {
        elements.add(new TextElement(TextElement.COMPOUND, otherText));
        return this;
    }

    public Text command(String message, String command) {
        elements.add(new TextElement(TextElement.COMMAND, message, command));
        return this;
    }

    public Text suggest(String message, String command) {
        elements.add(new TextElement(TextElement.SUGGEST, message, command));
        return this;
    }

    public List<TextElement> getElements() {
        return elements;
    }

    public String getPlainText() {
        StringBuilder sb = new StringBuilder();
        for (TextElement element : elements) {
            if (element.type == TextElement.TEXT) {
                sb.append(element.data);
            } else if (element.type == TextElement.COMPOUND) {
                Text childText = (Text)element.data;
                sb.append(childText.getPlainText());
            } else if (element.type == TextElement.COMMAND || element.type == TextElement.SUGGEST) {
                sb.append(element.data);
            }
        }

        return sb.toString();
    }
}


