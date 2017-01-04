package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class HealthContent implements KitContent<Integer> {
    public static final float DEFAULT_HEALTH = 20;

    private final int result;

    public HealthContent(double result) {
        this((int) result * 2);
    }

    public HealthContent(int result) {
        this.result = result;
    }

    @Override
    public void apply(GamePlayer player) {
        player.getPlayer().getBukkit().setHealth(this.getResult());
    }

    @Override
    public Integer getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<HealthContent> {
        @Override
        public HealthContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null && XMLParser.parseBoolean(reset.getValue())) {
                return new HealthContent(DEFAULT_HEALTH);
            }

            try {
                return new HealthContent(Double.parseDouble(xml.getTextNormalize()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
