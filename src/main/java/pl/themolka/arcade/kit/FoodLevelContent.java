package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class FoodLevelContent implements KitContent<Integer> {
    public static final int DEFAULT_LEVEL = 20;

    private final int result;

    public FoodLevelContent(double result) {
        this((int) result * 2);
    }

    public FoodLevelContent(int result) {
        this.result = result;
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().setFoodLevel(this.getResult());
    }

    @Override
    public Integer getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<FoodLevelContent> {
        @Override
        public FoodLevelContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null && XMLParser.parseBoolean(reset.getValue())) {
                return new FoodLevelContent(DEFAULT_LEVEL);
            }

            try {
                return new FoodLevelContent(Double.parseDouble(xml.getTextNormalize()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}