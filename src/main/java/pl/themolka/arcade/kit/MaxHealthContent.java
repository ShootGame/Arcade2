package pl.themolka.arcade.kit;

import org.bukkit.entity.Player;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class MaxHealthContent implements RemovableKitContent<Integer> {
    public static final int DEFAULT_HEALTH = 20;

    private final int result;

    public MaxHealthContent(double result) {
        this((int) result * 2);
    }

    public MaxHealthContent(int result) {
        this.result = result;
    }

    @Override
    public void attach(GamePlayer player, Integer value) {
        Player bukkit = player.getBukkit();
        if (bukkit != null) {
            bukkit.setMaxHealth(value);
        }
    }

    @Override
    public Integer defaultValue() {
        return DEFAULT_HEALTH;
    }

    @Override
    public Integer getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<MaxHealthContent> {
        @Override
        public MaxHealthContent parse(Element xml) throws DataConversionException {
            Attribute attribute = xml.getAttribute("reset");
            if (attribute != null && XMLParser.parseBoolean(attribute.getValue())) {
                return new MaxHealthContent(DEFAULT_HEALTH);
            }

            try {
                return new MaxHealthContent(Double.parseDouble(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
