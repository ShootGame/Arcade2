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

public class KnockbackContent implements RemovableKitContent<Float> {
    public static float DEFAULT_KNOCKBACK = 0F;

    private final float result;

    public KnockbackContent(float result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Float value) {
        player.getBukkit().setKnockbackReduction(value);
    }

    @Override
    public Float defaultValue() {
        return DEFAULT_KNOCKBACK;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    @KitContentLegacyParser
    public static class LegacyParser implements KitContentParser<KnockbackContent> {
        @Override
        public KnockbackContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null && XMLParser.parseBoolean(reset.getValue(), false)) {
                return new KnockbackContent(DEFAULT_KNOCKBACK);
            }

            try {
                return new KnockbackContent(Float.parseFloat(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    @NestedParserName({"knockback", "knockback-reduction", "knockbackreduction"})
    @Produces(KnockbackContent.class)
    public static class ContentParser extends BaseRemovableContentParser<KnockbackContent>
                                      implements InstallableParser {
        private Parser<Float> knockbackParser;

        @Override
        public void install(ParserContext context) {
            super.install(context);
            this.knockbackParser = context.type(Float.class);
        }

        @Override
        protected ParserResult<KnockbackContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new KnockbackContent(DEFAULT_KNOCKBACK));
            }

            float knockback = this.knockbackParser.parse(node).orFail();
            // TODO test if knockback can be negative
            return ParserResult.fine(node, name, value, new KnockbackContent(knockback));
        }
    }
}
