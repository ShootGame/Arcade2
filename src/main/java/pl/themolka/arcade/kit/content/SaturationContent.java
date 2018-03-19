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

public class SaturationContent implements RemovableKitContent<Float> {
    public static final float DEFAULT_SATURATION = 5F;

    private final float result;

    public SaturationContent(float result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Float value) {
        player.getBukkit().setSaturation(value);
    }

    @Override
    public Float defaultValue() {
        return DEFAULT_SATURATION;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    public static class LegacyParser implements LegacyKitContentParser<SaturationContent> {
        @Override
        public SaturationContent parse(Element xml) throws DataConversionException {
            Attribute attribute = xml.getAttribute("reset");
            if (attribute != null && XMLParser.parseBoolean(attribute.getValue(), false)) {
                return new SaturationContent(DEFAULT_SATURATION);
            }

            try {
                return new SaturationContent(Float.parseFloat(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    @NestedParserName("saturation")
    @Produces(SaturationContent.class)
    public static class ContentParser extends BaseRemovableContentParser<SaturationContent>
                                      implements InstallableParser {
        private Parser<Float> saturationParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.saturationParser = context.type(Float.class);
        }

        @Override
        protected ParserResult<SaturationContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new SaturationContent(DEFAULT_SATURATION));
            }

            float saturation = this.saturationParser.parse(node).orFail();
            // TODO test if saturation can be negative
            return ParserResult.fine(node, name, value, new SaturationContent(saturation));
        }
    }
}
