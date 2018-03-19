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
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.xml.XMLParser;

public class WalkSpeedContent implements RemovableKitContent<Float> {
    public static final float DEFAULT_SPEED = 0.2F;

    public static final float MIN_VALUE = -1F;
    public static final float MAX_VALUE = +1F;

    public static boolean testValue(float value) {
        return value >= MIN_VALUE && value <= MAX_VALUE;
    }

    private final float result;

    public WalkSpeedContent(float result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Float value) {
        player.getBukkit().setWalkSpeed(value);
    }

    @Override
    public Float defaultValue() {
        return DEFAULT_SPEED;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    public static class LegacyParser implements LegacyKitContentParser<WalkSpeedContent> {
        @Override
        public WalkSpeedContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null && XMLParser.parseBoolean(reset.getValue(), false)) {
                return new WalkSpeedContent(DEFAULT_SPEED);
            }

            try {
                return new WalkSpeedContent(Float.parseFloat(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    @NestedParserName({"walk-speed", "walkspeed"})
    @Produces(WalkSpeedContent.class)
    public static class ContentParser extends BaseRemovableContentParser<WalkSpeedContent>
                                      implements InstallableParser {
        private Parser<Float> speedParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.speedParser = context.type(Float.class);
        }

        @Override
        protected ParserResult<WalkSpeedContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new WalkSpeedContent(DEFAULT_SPEED));
            }

            float speed = this.speedParser.parse(node).orFail();
            if (speed < MIN_VALUE) {
                throw this.fail(node, name, value, "Walk speed is too slow (min " + MIN_VALUE + ")");
            } else if (speed > MAX_VALUE) {
                throw this.fail(node, name, value, "Walk speed is too fast (max " + MAX_VALUE + ")");
            }

            return ParserResult.fine(node, name, value, new WalkSpeedContent(speed));
        }
    }
}
