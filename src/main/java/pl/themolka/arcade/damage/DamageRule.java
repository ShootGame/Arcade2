package pl.themolka.arcade.damage;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.GamePlayer;

public class DamageRule implements Cancelable {
    public static final double ZERO = 0D;
    public static final double DEFAULT_DAMAGE = 1D;

    private final Filter entityFilter;
    private final Filter playerFilter;
    private double damage = ZERO;
    private double multiplier = ZERO;

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
        this.damage = cancel ? ZERO : DEFAULT_DAMAGE;
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

    public double getDamage(double firstDamage) {
        double result = firstDamage;
        if (this.hasMultiplier()) {
            result += this.getDamage();
            result *= this.getMultiplier();
        }

        return result;
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    public boolean hasMultiplier() {
        return this.multiplier != ZERO;
    }

    public boolean matches(Entity entity, EntityDamageEvent.DamageCause cause) {
        return !entity.isDead() || this.getEntityFilter().filter(entity, cause).isAllowed();
    }

    public boolean matches(GamePlayer player, EntityDamageEvent.DamageCause cause) {
        return player == null || !player.isOnline() || this.getPlayerFilter().filter(player, cause).isAllowed();
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}
