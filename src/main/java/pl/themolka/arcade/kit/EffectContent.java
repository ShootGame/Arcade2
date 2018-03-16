package pl.themolka.arcade.kit;

import org.bukkit.potion.PotionEffect;
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
import pl.themolka.arcade.potion.XMLPotionEffect;

public class EffectContent implements KitContent<PotionEffect> {
    private final PotionEffect result;

    public EffectContent(PotionEffect result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().addPotionEffect(this.getResult(), true);
    }

    @Override
    public PotionEffect getResult() {
        return this.result;
    }

    @KitContentLegacyParser
    public static class LegacyParser implements KitContentParser<EffectContent> {
        @Override
        public EffectContent parse(Element xml) throws DataConversionException {
            return new EffectContent(XMLPotionEffect.parse(xml));
        }
    }

    @NestedParserName({"effect", "potion-effect", "potioneffect"})
    @Produces(EffectContent.class)
    public static class ContentParser extends BaseContentParser<EffectContent>
                                      implements InstallableParser {
        private Parser<PotionEffect> effectParser;

        @Override
        public void install(ParserContext context) {
            this.effectParser = context.type(PotionEffect.class);
        }

        @Override
        protected ParserResult<EffectContent> parseNode(Node node, String name, String value) throws ParserException {
            PotionEffect effect = this.effectParser.parse(node).orFail();
            return ParserResult.fine(node, name, value, new EffectContent(effect));
        }
    }
}
