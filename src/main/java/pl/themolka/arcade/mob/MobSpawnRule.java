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
    private boolean cancel;
    private final Filter filter;

    @Deprecated
    public MobSpawnRule(Filter filter) {
        this.filter = Filters.secure(filter);
    }

    @Deprecated
    public MobSpawnRule(Filter filter, boolean cancel) {
        this(filter);
        this.cancel = cancel;
    }

    protected MobSpawnRule(Config config) {
        this.filter = Filters.secure(config.filter().getIfPresent());
        this.cancel = config.cancel();
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public boolean matches(Entity entity, CreatureSpawnEvent.SpawnReason reason, Location location) {
        return this.filter.filter(entity, reason, location).isTrue();
    }

    public interface Config extends IGameConfig<MobSpawnRule> {
        default Ref<Filter> filter() { return Ref.empty(); }
        default boolean cancel() { return true; }

        @Override
        default MobSpawnRule create(Game game) {
            return new MobSpawnRule(this);
        }
    }
}
