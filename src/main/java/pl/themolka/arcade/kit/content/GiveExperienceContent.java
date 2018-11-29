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
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class GiveExperienceContent implements KitContent<Integer> {
    public static boolean testValue(int value) {
        return value != 0;
    }

    private final int result;

    protected GiveExperienceContent(Config config) {
        this.result = config.result().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().giveExp(this.result);
    }

    @Override
    public Integer getResult() {
        return this.result;
    }

    @NestedParserName({"give-experience", "giveexperience", "give-experiences", "giveexperiences",
                       "take-experience", "takeexperience", "take-experiences", "takeexperiences"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<Integer> experienceParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.experienceParser = context.type(Integer.class);
        }

        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            int experience = this.experienceParser.parseWithDefinition(node, name, value).orFail();
            if (experience <= 0) {
                throw this.fail(node, name, value, "Experience must be positive (greater than 0)");
            }

            if (name.toLowerCase().startsWith("take")) {
                experience = -experience;
            }

            final int finalExperience = experience;
            return Result.fine(node, name, value, new Config() {
                public Ref<Integer> result() { return Ref.ofProvided(finalExperience); }
            });
        }
    }

    public interface Config extends KitContent.Config<GiveExperienceContent, Integer> {
        @Override
        default GiveExperienceContent create(Game game, Library library) {
            return new GiveExperienceContent(this);
        }
    }
}
