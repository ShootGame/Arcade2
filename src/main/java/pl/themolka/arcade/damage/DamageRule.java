package pl.themolka.arcade.damage;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.util.Percentage;

public class DamageRule implements Cancelable {
    public static final double DENY_DAMAGE = 0D;

    public static final double DEFAULT_DAMAGE = DENY_DAMAGE;
    public static final Percentage DEFAULT_MULTIPLIER = Percentage.DONE;

    private final Filter entityFilter;
    private final Filter playerFilter;
    private double damage = DEFAULT_DAMAGE;
    private Percentage multiplier = DEFAULT_MULTIPLIER;

    public DamageRule(Config config) {
        this(config.entityFilter().get(), config.playerFilter().get());
        this.setDamage(config.damage());
        this.setMultiplier(config.multiplier());
    }

    protected DamageRule(Filter filter) {
        this(filter, filter);
    }

    protected DamageRule(Filter entityFilter, Filter playerFilter) {
        this.entityFilter = Filters.secure(entityFilter);
        this.playerFilter = Filters.secure(playerFilter);
    }

    @Override
    public boolean isCanceled() {
        return this.damage <= DENY_DAMAGE;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.damage = cancel ? DENY_DAMAGE : 1D;
    }

    public Filter getEntityFilter() {
        return this.entityFilter;
    }

    public Filter getPlayerFilter() {
        return this.playerFilter;
    }

    public double getDamage() {
        return this.isCanceled() ? DENY_DAMAGE : this.damage;
    }

    public double getDamage(double firstDamage) {
        return this.multiplier.preprocess(firstDamage + this.getDamage());
    }

    public Percentage getMultiplier() {
        return this.multiplier;
    }

    public boolean matches(Entity entity, EntityDamageEvent.DamageCause cause) {
        return !entity.isDead() && this.entityFilter.filter(entity, cause).isAllowed();
    }

    public boolean matches(GamePlayer player, EntityDamageEvent.DamageCause cause) {
        return player != null && player.isOnline() && this.playerFilter.filter(player, cause).isAllowed();
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setMultiplier(Percentage multiplier) {
        this.multiplier = multiplier != null ? multiplier : DEFAULT_MULTIPLIER;
    }

    public interface Config extends IGameConfig<DamageRule> {
        default Ref<Filter> entityFilter() { return Ref.ofProvided(Filters.undefined()); }
        default Ref<Filter> playerFilter() { return Ref.ofProvided(Filters.undefined()); }
        default double damage() { return DEFAULT_DAMAGE; }
        default Percentage multiplier() { return DEFAULT_MULTIPLIER; }

        @Override
        default DamageRule create(Game game) {
            return new DamageRule(this);
        }
    }
}
