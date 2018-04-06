package pl.themolka.arcade.mob;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;

public class MobSpawnRule implements Cancelable {
    private final boolean cancel;
    private final Filter filter;

    protected MobSpawnRule(Game game, IGameConfig.Library library, Config config) {
        this.filter = Filters.secure(library.getOrDefine(game, config.filter().getIfPresent()));
        this.cancel = config.cancel();
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        throw new UnsupportedOperationException("Cannot cancel mob spawn rule");
    }

    public Filter getFilter() {
        return this.filter;
    }

    public boolean matches(Entity entity, CreatureSpawnEvent.SpawnReason reason, Location location) {
        return this.filter.filter(entity, reason, location).isTrue();
    }

    public interface Config extends IGameConfig<MobSpawnRule> {
        default Ref<Filter.Config<?>> filter() { return Ref.empty(); }
        default boolean cancel() { return true; }

        @Override
        default MobSpawnRule create(Game game, Library library) {
            return new MobSpawnRule(game, library, this);
        }
    }
}
