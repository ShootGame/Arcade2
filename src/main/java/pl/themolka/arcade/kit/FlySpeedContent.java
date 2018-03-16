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

public class FlySpeedContent implements RemovableKitContent<Float> {
    public static final float DEFAULT_SPEED = 0.1F;

    public static final float MIN_VALUE = -1F;
    public static final float MAX_VALUE = +1F;

    public static boolean testValue(float value) {
        return value <= MIN_VALUE && value >= MAX_VALUE;
    }

    private final float result;

    public FlySpeedContent(float result) {
        this.result = result;
    }

    public FlySpeedContent(int result) {
        this((float) result);
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Float value) {
        player.getBukkit().setFlySpeed(value);
    }

    @Override
    public Float defaultValue() {
        return DEFAULT_SPEED;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    @KitContentLegacyParser
    public static class LegacyParser implements KitContentParser<FlySpeedContent> {
        @Override
        public FlySpeedContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null) {
                return new FlySpeedContent(DEFAULT_SPEED);
            }

            try {
                return new FlySpeedContent(Integer.parseInt(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    @NestedParserName({"fly-speed", "flyspeed"})
    @Produces(FlySpeedContent.class)
    public static class ContentParser extends BaseRemovableContentParser<FlySpeedContent>
                                      implements InstallableParser {
        private Parser<Float> speedParser;

        @Override
        public void install(ParserContext context) {
            super.install(context);
            this.speedParser = context.type(Float.class);
        }

        @Override
        protected ParserResult<FlySpeedContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new FlySpeedContent(DEFAULT_SPEED));
            }

            float speed = this.speedParser.parse(node).orFail();
            if (speed < MIN_VALUE) {
                throw this.fail(node, name, value, "Fly speed is too slow (min " + MIN_VALUE + ")");
            } else if (speed > MAX_VALUE) {
                throw this.fail(node, name, value, "Fly speed is too fast (max " + MAX_VALUE + ")");
            }

            return ParserResult.fine(node, name, value, new FlySpeedContent(speed));
        }
    }
}
