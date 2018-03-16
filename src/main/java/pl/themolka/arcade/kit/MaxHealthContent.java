package pl.themolka.arcade.kit;

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

public class MaxHealthContent implements RemovableKitContent<Double> {
    public static final double DEFAULT_HEALTH = 20;

    public static final double MIN_VALUE = 0.0;

    public static boolean testValue(double value) {
        return value > MIN_VALUE;
    }

    private final double result;

    public MaxHealthContent(double result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Double value) {
        player.getBukkit().setMaxHealth(value);
    }

    @Override
    public Double defaultValue() {
        return DEFAULT_HEALTH;
    }

    @Override
    public Double getResult() {
        return this.result;
    }

    @KitContentLegacyParser
    public static class LegacyParser implements KitContentParser<MaxHealthContent> {
        @Override
        public MaxHealthContent parse(Element xml) throws DataConversionException {
            Attribute attribute = xml.getAttribute("reset");
            if (attribute != null && XMLParser.parseBoolean(attribute.getValue(), false)) {
                return new MaxHealthContent(DEFAULT_HEALTH);
            }

            try {
                return new MaxHealthContent(Double.parseDouble(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    @NestedParserName({"max-health", "maxhealth"})
    @Produces(MaxHealthContent.class)
    public static class ContentParser extends BaseRemovableContentParser<MaxHealthContent>
                                      implements InstallableParser {
        private Parser<Double> maxHealthParser;

        @Override
        public void install(ParserContext context) {
            super.install(context);
            this.maxHealthParser = context.type(Double.class);
        }

        @Override
        protected ParserResult<MaxHealthContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new MaxHealthContent(DEFAULT_HEALTH));
            }

            double maxHealth = this.maxHealthParser.parse(node).orFail();
            if (maxHealth <= MIN_VALUE) {
                throw this.fail(node, name, value, "Max health cannot be negative (smaller than 0)");
            }

            return ParserResult.fine(node, name, value, new MaxHealthContent(maxHealth));
        }
    }
}
