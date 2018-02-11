package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class HealthContent implements RemovableKitContent<Double> {
    public static final double DEFAULT_HEALTH = 20D;

    private final double result;

    public HealthContent(double result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Double value) {
        player.getBukkit().setHealth(value);
    }

    @Override
    public Double defaultValue() {
        return DEFAULT_HEALTH;
    }

    @Override
    public Double getResult() {
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
                return new HealthContent(Double.parseDouble(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
