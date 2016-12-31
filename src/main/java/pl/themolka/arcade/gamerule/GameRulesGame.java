package pl.themolka.arcade.gamerule;

import org.bukkit.World;
import pl.themolka.arcade.game.GameModule;

import java.util.List;

public class GameRulesGame extends GameModule {
    private final List<GameRule> rules;

    public GameRulesGame(List<GameRule> rules) {
        this.rules = rules;
    }

    @Override
    public void onEnable() {
        World world = this.getGame().getMap().getWorld();
        for (GameRule rule : this.getRules()) {
            if (!rule.getType().isApplicable()) {
                continue;
            }

            world.setGameRuleValue(rule.getType().getKey(), rule.getValue());
        }

        this.getGame().setMetadata(GameRulesModule.class, GameRulesModule.METADATA_GAMERULES, this.getRules());
    }

    public List<GameRule> getRules() {
        return this.rules;
    }
}
