package pl.themolka.arcade.gamerule;

import org.bukkit.World;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.ArrayList;
import java.util.List;

public class GameRulesGame extends GameModule {
    private final List<GameRule> rules = new ArrayList<>();

    protected GameRulesGame(Config config) {
        this.rules.addAll(config.rules());
    }

    @Override
    public void onEnable() {
        World world = this.getGame().getMap().getWorld();
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

    public List<GameRule> getRules() {
        return new ArrayList<>(this.rules);
    }

    public interface Config extends IGameModuleConfig<GameRulesGame> {
        List<GameRule> rules();

        @Override
        default GameRulesGame create(Game game, Library library) {
            return new GameRulesGame(this);
        }
    }
}
