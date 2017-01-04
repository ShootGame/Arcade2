package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class SaturationContent implements KitContent<Integer> {
    public static final int DEFAULT_SATURATION = 1;

    private final int result;

    public SaturationContent(float result) {
        this((int) result * 2);
    }

    public SaturationContent(int result) {
        this.result = result;
    }

    @Override
    public void apply(GamePlayer player) {
        player.getPlayer().getBukkit().setSaturation(this.getResult());
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
                return new SaturationContent(Float.parseFloat(xml.getTextNormalize()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
