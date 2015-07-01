package SpongeBridge;

import com.google.common.base.Optional;
import org.spongepowered.api.text.Text;
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
        Text.Literal lit = new Text.Literal(TextColors.BLACK, TextStyles.NONE,null,null,null,null,string);
        return (Text)lit;
    }

    public static Text getText(org.bukkit.Text object) {
        Text.Literal lit = new Text.Literal(TextColors.BLACK, TextStyles.NONE,null,null,null,null,"getText of text object stub");
        return (Text)lit;
    }
}
