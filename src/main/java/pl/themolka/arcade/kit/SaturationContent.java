package pl.themolka.arcade.kit;

import org.bukkit.entity.Player;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class SaturationContent implements RemovableKitContent<Integer> {
    public static final int DEFAULT_SATURATION = 5;

    private final int result;

    public SaturationContent(float result) {
        this((int) result * 2);
    }

    public SaturationContent(int result) {
        this.result = result;
    }

    @Override
    public void attach(GamePlayer player, Integer value) {
        Player bukkit = player.getBukkit();
        if (bukkit != null) {
            bukkit.setSaturation(value);
        }
    }

    @Override
    public Integer defaultValue() {
        return DEFAULT_SATURATION;
    }

    @Override
    public Integer getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<SaturationContent> {
        @Override
        public SaturationContent parse(Element xml) throws DataConversionException {
            Attribute attribute = xml.getAttribute("reset");
            if (attribute != null && XMLParser.parseBoolean(attribute.getValue())) {
                return new SaturationContent(DEFAULT_SATURATION);
            }

            try {
                return new SaturationContent(Float.parseFloat(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
