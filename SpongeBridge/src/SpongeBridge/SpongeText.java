package SpongeBridge;

import com.google.common.base.Optional;
import org.bukkit.ChatColor;
import org.bukkit.ChatStyle;
import org.spongepowered.api.text.Text;

/**
 * Created by solum on 5/2/2015.
 */
public class SpongeText {
    public static Optional<Text> getOptionalText(String string) {
        Optional<Text> opt = Optional.of(SpongeText.getText(string));
        return opt;
    }

    public static Text getText(String string) {
        return getText(org.bukkit.Text.make().text(string));
    }

    public static Text getText(org.bukkit.Text object) {
        Text.Builder builder = Text.builder();
        ChatColor curColor = null;
        ChatStyle curStyle = null;
        for (org.bukkit.Text.TextElement element : object.getElements()) {
            if (element.type == org.bukkit.Text.TextElement.COLOR) {
                curColor = (ChatColor) element.data;
            } else if (element.type == org.bukkit.Text.TextElement.TEXT) {
                Text.Builder text = Text.builder((String)element.data);
                if (curColor != null) {
                    text.color(curColor.getValue());
                }
                if (curStyle != null) {
                    text.style(curStyle.getValue());
                }
                builder.append(text.build());
            } else if (element.type == org.bukkit.Text.TextElement.STYLE) {
                curStyle = (ChatStyle)element.data;
            } else if (element.type == org.bukkit.Text.TextElement.COMPOUND) {
                builder.append(getText((org.bukkit.Text)element.data));
            }
        }
        return builder.build();
    }
}
