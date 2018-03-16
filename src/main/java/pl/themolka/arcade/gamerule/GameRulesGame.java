package pl.themolka.arcade.gamerule;

import org.bukkit.World;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.ArrayList;
import java.util.List;

public class GameRulesGame extends GameModule {
    private final List<GameRule> rules = new ArrayList<>();

    @Deprecated
    public GameRulesGame(List<GameRule> rules) {
        this.rules.addAll(rules);
    }

    protected GameRulesGame(Config config) {
        this.rules.addAll(config.rules());
    }

    @Override
    public void onEnable() {
        World world = this.getGame().getMap().getWorld();
        for (GameRule rule : this.rules) {
            GameRuleType type = rule.getType();
            if (type.isApplicable()) {
                world.setGameRuleValue(type.getKey(), rule.getValue());
            }
        }
    }

    public List<GameRule> getRules() {
        return new ArrayList<>(this.rules);
    }

    public interface Config extends IGameModuleConfig<GameRulesGame> {
        List<GameRule> rules();

        @Override
        default GameRulesGame create(Game game) {
            return new GameRulesGame(this);
        }
    }
}
