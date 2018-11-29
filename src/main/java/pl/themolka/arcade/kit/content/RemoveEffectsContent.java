package pl.themolka.arcade.kit.content;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class RemoveEffectsContent implements BaseVoidKitContent {
    protected RemoveEffectsContent() {
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        Player bukkit = player.getBukkit();
        for (PotionEffect effect : bukkit.getActivePotionEffects()) {
            bukkit.removePotionEffect(effect.getType());
        }
    }

    @NestedParserName({"remove-effects", "removeeffects"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config> {
        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, value, new Config() {});
        }
    }

    public interface Config extends BaseVoidKitContent.Config<RemoveEffectsContent> {
        @Override
        default RemoveEffectsContent create(Game game, Library library) {
            return new RemoveEffectsContent();
        }
    }
}
