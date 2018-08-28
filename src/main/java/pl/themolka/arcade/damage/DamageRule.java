package pl.themolka.arcade.damage;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.config.Unique;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.util.Percentage;
import pl.themolka.arcade.util.StringId;

public class DamageRule implements Cancelable, StringId {
    private final double damage;
    private final Filter entityFilter;
    private final String id;
    private final Percentage multiplier;
    private final Filter playerFilter;

    protected DamageRule(Game game, IGameConfig.Library library, Config config) {
        this.damage = config.damage().get();
        this.entityFilter = Filters.secure(library.getOrDefine(game, config.entityFilter().getIfPresent()));
        this.id = config.id();
        this.multiplier = config.multiplier().get();
        this.playerFilter = Filters.secure(library.getOrDefine(game, config.playerFilter().getIfPresent()));
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isCanceled() {
        return this.damage <= Config.DENY_DAMAGE;
    }

    @Override
    public void setCanceled(boolean cancel) {
        throw new UnsupportedOperationException("Cannot cancel damage rule");
    }

    public double getDamage() {
        return this.isCanceled() ? Config.DENY_DAMAGE
                                 : this.damage;
    }

    public double getDamage(double firstDamage) {
        return this.multiplier.calculate(firstDamage + this.getDamage());
    }

    public Filter getEntityFilter() {
        return this.entityFilter;
    }

    public Percentage getMultiplier() {
        return this.multiplier;
    }

    public Filter getPlayerFilter() {
        return this.playerFilter;
    }

    public boolean matches(Entity entity, EntityDamageEvent.DamageCause cause) {
        return !entity.isDead() && this.entityFilter.filter(entity, cause).isTrue();
    }

    public boolean matches(GamePlayer player, EntityDamageEvent.DamageCause cause) {
        return player != null && player.isOnline() && this.playerFilter.filter(player, cause).isTrue();
    }

    public interface Config extends IGameConfig<DamageRule>, Unique {
        double DENY_DAMAGE = 0D;

        double DEFAULT_DAMAGE = DENY_DAMAGE;
        Percentage DEFAULT_MULTIPLIER = Percentage.DONE;

        default Ref<Double> damage() { return Ref.ofProvided(DEFAULT_DAMAGE); }
        default Ref<Filter.Config<?>> entityFilter() { return Ref.empty(); }
        default Ref<Percentage> multiplier() { return Ref.ofProvided(DEFAULT_MULTIPLIER); }
        default Ref<Filter.Config<?>> playerFilter() { return Ref.empty(); }

        @Override
        default DamageRule create(Game game, Library library) {
            return new DamageRule(game, library, this);
        }
    }
}
