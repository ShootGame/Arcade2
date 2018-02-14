package pl.themolka.arcade.damage;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.GamePlayer;

public class DamageRule implements Cancelable {
    public static final double ZERO = 0D;

    private final Filter entityFilter;
    private final Filter playerFilter;
    private double damage;

    public DamageRule(Filter filter) {
        this(filter, filter);
    }

    public DamageRule(Filter filter, double damage) {
        this(filter, filter, damage);
    }

    public DamageRule(Filter entityFilter, Filter playerFilter) {
        this.entityFilter = Filters.secure(entityFilter);
        this.playerFilter = Filters.secure(playerFilter);
    }

    public DamageRule(Filter entityFilter, Filter playerFilter, double damage) {
        this(entityFilter, playerFilter);
        this.damage = damage;
    }

    @Override
    public boolean isCanceled() {
        return this.damage <= ZERO;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.damage = ZERO;
    }

    public Filter getEntityFilter() {
        return this.entityFilter;
    }

    public Filter getPlayerFilter() {
        return this.playerFilter;
    }

    public double getDamage() {
        return this.isCanceled() ? ZERO : this.damage;
    }

    public boolean matches(Entity entity, EntityDamageEvent.DamageCause cause, Object damager) {
        return !entity.isDead() || this.getEntityFilter().filter(entity, cause, damager).isDenied();
    }

    public boolean matches(GamePlayer player, EntityDamageEvent.DamageCause cause, Object damager) {
        return player == null || !player.isOnline() || this.getPlayerFilter().filter(player, cause, damager).isDenied();
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
