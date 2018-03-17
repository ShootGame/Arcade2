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

public class HealthContent implements RemovableKitContent<Double> {
    public static final double DEFAULT_HEALTH = 20D;

    public static final double MIN_VALUE = 0F;

    public static boolean testValue(double value) {
        return value >= MIN_VALUE;
    }

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

    public static class LegacyParser implements LegacyKitContentParser<HealthContent> {
        @Override
        public HealthContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null && XMLParser.parseBoolean(reset.getValue(), false)) {
                return new HealthContent(DEFAULT_HEALTH);
            }

            try {
                return new HealthContent(Double.parseDouble(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    @NestedParserName({"health", "hearts", "heart"})
    @Produces(HealthContent.class)
    public static class ContentParser extends BaseRemovableContentParser<HealthContent>
                                      implements InstallableParser {
        private Parser<Double> healthParser;

        @Override
        public void install(ParserContext context) {
            super.install(context);
            this.healthParser = context.type(Double.class);
        }

        @Override
        protected ParserResult<HealthContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new HealthContent(DEFAULT_HEALTH));
            }

            double health = this.healthParser.parse(node).orFail();
            if (health < MIN_VALUE) {
                throw this.fail(node, name, value, "Health cannot be negative (smaller than 0)");
            }

            return ParserResult.fine(node, name, value, new HealthContent(health));
        }
    }
}
