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
import pl.themolka.arcade.util.Percentage;

public class ExperienceContent implements RemovableKitContent<Percentage> {
    public static boolean testValue(Percentage value) {
        return value.isNormalized();
    }

    private final Percentage result;

    protected ExperienceContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue()).trim();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Percentage value) {
        player.getBukkit().setExp((float) value.getValue());
    }

    @Override
    public Percentage defaultValue() {
        return Config.DEFAULT_EXPERIENCE;
    }

    @Override
    public Percentage getResult() {
        return this.result;
    }

    @NestedParserName({"experience", "exp"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Percentage> experienceParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.experienceParser = context.type(Percentage.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Percentage> result() { return Ref.empty(); }
                });
            }

            Percentage experience = this.experienceParser.parseWithDefinition(node, name, value).orFail();

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Percentage> result() { return Ref.ofProvided(experience); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<ExperienceContent, Percentage> {
        Percentage DEFAULT_EXPERIENCE = Percentage.ZERO.trim();

        @Override
        default ExperienceContent create(Game game, Library library) {
            return new ExperienceContent(this);
        }
    }
}
