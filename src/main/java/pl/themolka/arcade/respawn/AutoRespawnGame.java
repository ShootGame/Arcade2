package pl.themolka.arcade.respawn;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.life.PlayerDeathEvent;
import pl.themolka.arcade.time.Time;

public class AutoRespawnGame extends GameModule {
    private final Filter filter;
    private final Time cooldown;

    protected AutoRespawnGame(Game game, IGameConfig.Library library, Config config) {
        this.filter = Filters.secure(library.getOrDefine(game, config.filter().getIfPresent()));
        this.cooldown = config.cooldown().get();
    }

    public boolean canAutoRespawn(GamePlayer victim) {
        return this.filter.filter(victim).isNotFalse();
    }

    public Filter getFilter() {
        return this.filter;
    }

    public Time getCooldown() {
        return this.cooldown;
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.canAutoRespawn(event.getVictim())) {
            event.setAutoRespawn(true, this.cooldown);
        }
    }

    public interface Config extends IGameModuleConfig<AutoRespawnGame> {
        Time DEFAULT_COOLDOWN = PlayerDeathEvent.DEFAULT_AUTO_RESPAWN_COOLDOWN;

        default Ref<Filter.Config<?>> filter() { return Ref.empty(); }
        default Ref<Time> cooldown() { return Ref.ofProvided(DEFAULT_COOLDOWN); }

        @Override
        default AutoRespawnGame create(Game game, Library library) {
            return new AutoRespawnGame(game, library, this);
        }
    }
}
