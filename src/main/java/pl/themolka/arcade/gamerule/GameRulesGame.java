package pl.themolka.arcade.gamerule;

import org.bukkit.World;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.LinkedHashSet;
import java.util.Set;

public class GameRulesGame extends GameModule {
    private final Set<GameRule> rules = new LinkedHashSet<>();

    protected GameRulesGame(Config config) {
        this.rules.addAll(config.rules().get());
    }

    @Override
    public void onEnable() {
        World world = this.getGame().getWorld();
        for (GameRule rule : this.rules) {
            GameRuleType type = GameRuleType.byKey(rule.getKey());
            if (type != null && !type.isApplicable()) {
                continue;
            }

            if (rule.isApplicable()) {
                rule.apply(world);
            }
        }
    }

    public Set<GameRule> getRules() {
        return new LinkedHashSet<>(this.rules);
    }

    public interface Config extends IGameModuleConfig<GameRulesGame> {
        Ref<Set<GameRule>> rules();

        @Override
        default GameRulesGame create(Game game, Library library) {
            return new GameRulesGame(this);
        }
    }
}
