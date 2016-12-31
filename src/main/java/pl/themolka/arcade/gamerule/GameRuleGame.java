package pl.themolka.arcade.gamerule;

import org.bukkit.World;
import pl.themolka.arcade.game.GameModule;

import java.util.List;

public class GameRuleGame extends GameModule {
    private final List<GameRule> rules;

    public GameRuleGame(List<GameRule> rules) {
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

        this.getGame().setMetadata(GameRuleModule.class, GameRuleModule.METADATA_GAMERULES, this.getRules());
    }

    public List<GameRule> getRules() {
        return this.rules;
    }
}
