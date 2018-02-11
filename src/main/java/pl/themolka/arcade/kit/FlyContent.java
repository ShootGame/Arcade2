package pl.themolka.arcade.kit;

import org.bukkit.entity.Player;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class FlyContent implements RemovableKitContent<Boolean> {
    public static final boolean DEFAULT_ALLOW_FLYING = false;
    public static final boolean DEFAULT_FLYING = false;

    private final boolean result;

    public FlyContent(boolean result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Boolean value) {
        Player bukkit = player.getBukkit();
        bukkit.setAllowFlight(value);

        if (!value) {
            bukkit.setFlying(DEFAULT_FLYING);
        }
    }

    @Override
    public Boolean defaultValue() {
        return DEFAULT_ALLOW_FLYING;
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<FlyContent> {
        @Override
        public FlyContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null) {
                return new FlyContent(DEFAULT_FLYING);
            }

            return new FlyContent(XMLParser.parseBoolean(xml.getValue()));
        }
    }
}
