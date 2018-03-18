package pl.themolka.arcade.kit.content;

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

public class EffectContent implements KitContent<PotionEffect>, BaseModeContent {
    private final PotionEffect result;
    private final Mode mode;

    public EffectContent(PotionEffect result) {
        this(result, Mode.GIVE);
    }

    public EffectContent(PotionEffect result, Mode mode) {
        this.result = result;
        this.mode = mode;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        if (this.give()) {
            player.getBukkit().addPotionEffect(this.getResult(), true);
        } else if (this.take()) {
            player.getBukkit().removePotionEffect(this.getResult().getType());
        }
    }

    @Override
    public PotionEffect getResult() {
        return this.result;
    }

    @Override
    public boolean give() {
        return this.mode.equals(Mode.GIVE);
    }

    @Override
    public boolean take() {
        return this.mode.equals(Mode.TAKE);
    }

    public static class LegacyParser implements LegacyKitContentParser<EffectContent> {
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
        private Parser<Mode> modeParser;

        @Override
        public void install(ParserContext context) {
            this.effectParser = context.type(PotionEffect.class);
            this.modeParser = context.type(Mode.class);
        }

        @Override
        protected ParserResult<EffectContent> parseNode(Node node, String name, String value) throws ParserException {
            PotionEffect effect = this.effectParser.parse(node).orFail();
            return ParserResult.fine(node, name, value, new EffectContent(effect, this.modeParser.parse(node).orFail()));
        }
    }
}
