package SpongeBridge;

import com.google.common.base.Optional;
import org.bukkit.ChatColor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

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
        TextBuilder builder = Texts.builder();
        ChatColor curColor = null;
        for (org.bukkit.Text.TextElement element : object.getElements()) {
            if (element.type == org.bukkit.Text.TextElement.COLOR) {
                curColor = (ChatColor) element.data;
            } else if (element.type == org.bukkit.Text.TextElement.TEXT) {
                TextBuilder text = Texts.builder((String)element.data);
                if (curColor != null) {
                    text.color(curColor.getValue());
                }
                builder.append(text.build());
            }
        }
        return builder.build();
    }
}
