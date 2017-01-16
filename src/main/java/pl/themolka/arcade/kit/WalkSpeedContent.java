package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class WalkSpeedContent implements KitContent<Float> {
    public static final float DEFAULT_SPEED = 1F;

    private final float result;

    public WalkSpeedContent(float result) {
        this.result = result;
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().setWalkSpeed(this.getResult());
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
                return new WalkSpeedContent(Float.parseFloat(xml.getTextNormalize()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
