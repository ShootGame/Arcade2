package pl.themolka.arcade.kit.content;

import org.bukkit.entity.Player;
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

public class CanFlyContent implements RemovableKitContent<Boolean> {
    private final boolean result;
    private final boolean force;

    protected CanFlyContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
        this.force = config.force().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Boolean value) {
        Player bukkit = player.getBukkit();
        bukkit.setAllowFlight(value);

        if (!this.force && !value) {
            bukkit.setFlying(false);
        }
    }

    @Override
    public Boolean defaultValue() {
        return Config.DEFAULT_CAN_FLY;
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    @NestedParserName({"can-fly", "canfly"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Boolean> canFlyParser;
        private Parser<Boolean> forceParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.canFlyParser = context.type(Boolean.class);
            this.forceParser = context.type(Boolean.class);
        }

        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return Result.fine(node, name, value, new Config() {
                    public Ref<Boolean> result() { return Ref.empty(); }
                });
            }

            boolean canFly = this.canFlyParser.parseWithDefinition(node, name, value).orFail();
            boolean force = this.forceParser.parse(node.property("force")).orDefault(Config.DEFAULT_FORCE);

            return Result.fine(node, name, value, new Config() {
                public Ref<Boolean> result() { return Ref.ofProvided(canFly); }
                public Ref<Boolean> force() { return Ref.ofProvided(force); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<CanFlyContent, Boolean> {
        boolean DEFAULT_CAN_FLY = false;
        boolean DEFAULT_FORCE = false;

        default Ref<Boolean> force() { return Ref.ofProvided(DEFAULT_FORCE); }

        @Override
        default CanFlyContent create(Game game, Library library) {
            return new CanFlyContent(this);
        }
    }
}
