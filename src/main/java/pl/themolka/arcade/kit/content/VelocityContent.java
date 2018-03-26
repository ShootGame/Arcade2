package pl.themolka.arcade.kit.content;

import org.bukkit.util.Vector;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class VelocityContent implements RemovableKitContent<Vector> {
    private final Vector result;

    protected VelocityContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Vector value) {
        player.getBukkit().setVelocity(value);
    }

    @Override
    public Vector defaultValue() {
        return Config.DEFAULT_VELOCITY;
    }

    @Override
    public Vector getResult() {
        return this.result;
    }

    @NestedParserName({"velocity", "push", "pull"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config> {
        private Parser<Vector> velocityParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.velocityParser = context.type(Vector.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Vector> result() { return Ref.empty(); }
                });
            }

            Vector velocity = this.velocityParser.parse(node).orFail();

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Vector> result() { return Ref.ofProvided(velocity); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<VelocityContent, Vector> {
        Vector DEFAULT_VELOCITY = new Vector(0, 0, 0);

        @Override
        default VelocityContent create(Game game) {
            return new VelocityContent(this);
        }
    }
}
