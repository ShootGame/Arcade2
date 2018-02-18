package pl.themolka.arcade.mob;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;

public class MobSpawnRule implements Cancelable {
    private boolean cancel;
    private final Filter filter;

    public MobSpawnRule(Filter filter) {
        this.filter = Filters.secure(filter);
    }

    public MobSpawnRule(Filter filter, boolean cancel) {
        this(filter);
        this.cancel = cancel;
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
        return this.getFilter().filter(entity, reason, location).isAllowed();
    }
}
