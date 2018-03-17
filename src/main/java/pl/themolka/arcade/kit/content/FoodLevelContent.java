package pl.themolka.arcade.kit.content;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.xml.XMLParser;

public class FoodLevelContent implements RemovableKitContent<Integer> {
    public static final int DEFAULT_LEVEL = 20;

    private final int result;

    // in chickens
    public FoodLevelContent(double result) {
        this((int) result * 2);
    }

    // in half chickens
    public FoodLevelContent(int result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Integer value) {
        player.getBukkit().setFoodLevel(value);
    }

    @Override
    public Integer defaultValue() {
        return DEFAULT_LEVEL;
    }

    @Override
    public Integer getResult() {
        return this.result;
    }

    public static class LegacyParser implements LegacyKitContentParser<FoodLevelContent> {
        @Override
        public FoodLevelContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null && XMLParser.parseBoolean(reset.getValue(), false)) {
                return new FoodLevelContent(DEFAULT_LEVEL);
            }

            try {
                return new FoodLevelContent(XMLParser.parseDouble(xml.getValue(), DEFAULT_LEVEL));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    @NestedParserName({"food-level", "foodlevel", "food"})
    @Produces(FoodLevelContent.class)
    public static class ContentParser extends BaseRemovableContentParser<FoodLevelContent>
                                      implements InstallableParser {
        private Parser<Double> levelParser;

        @Override
        public void install(ParserContext context) {
            super.install(context);
            this.levelParser = context.type(Double.class);
        }

        @Override
        protected ParserResult<FoodLevelContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new FoodLevelContent(DEFAULT_LEVEL));
            }

            double level = this.levelParser.parse(node).orFail();
            // TODO test if level can be negative
            return ParserResult.fine(node, name, value, new FoodLevelContent(level));
        }
    }
}
