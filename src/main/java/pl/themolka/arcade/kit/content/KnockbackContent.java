package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class KnockbackContent implements RemovableKitContent<Float> {
    private final float result;

    protected KnockbackContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
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
        return Config.DEFAULT_KNOCKBACK;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    @NestedParserName({"knockback", "knockback-reduction", "knockbackreduction", "knock"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Float> knockbackParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.knockbackParser = context.type(Float.class);
        }

        @Override
        protected ParserResult<Config> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Float> result() { return Ref.empty(); }
                });
            }

            float knockback = this.knockbackParser.parseWithDefinition(node, name, value).orFail();

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Float> result() { return Ref.ofProvided(knockback); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<KnockbackContent, Float> {
        float DEFAULT_KNOCKBACK = 0F;

        @Override
        default KnockbackContent create(Game game, Library library) {
            return new KnockbackContent(this);
        }
    }
}
