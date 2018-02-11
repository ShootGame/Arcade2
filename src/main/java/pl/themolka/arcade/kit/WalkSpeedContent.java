package pl.themolka.arcade.kit;

import org.bukkit.entity.Player;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class WalkSpeedContent implements RemovableKitContent<Float> {
    public static final float DEFAULT_SPEED = 0.2F;

    private final float result;

    public WalkSpeedContent(float result) {
        this.result = result;
    }

    @Override
    public void attach(GamePlayer player, Float value) {
        Player bukkit = player.getBukkit();
        if (bukkit != null) {
            bukkit.setWalkSpeed(value);
        }
    }

    @Override
    public Float defaultValue() {
        return DEFAULT_SPEED;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<WalkSpeedContent> {
        @Override
        public WalkSpeedContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null && XMLParser.parseBoolean(reset.getValue())) {
                return new WalkSpeedContent(DEFAULT_SPEED);
            }

            try {
                return new WalkSpeedContent(Float.parseFloat(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
