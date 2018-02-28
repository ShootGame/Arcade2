package pl.themolka.arcade.damage;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.themolka.arcade.config.IConfig;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.util.Percentage;

import java.util.Objects;

public class DamageRule implements Cancelable {
    public static final double DENY_DAMAGE = 0D;

    private final Config config;

    public DamageRule(Config config) {
        this.config = Objects.requireNonNull(config, "config cannot be null");
    }

    @Override
    public boolean isCanceled() {
        return this.config.damage() <= DENY_DAMAGE;
    }

    @Override
    public void setCanceled(boolean cancel) {
    }

    public Config getConfig() {
        return this.config;
    }

    public double getDamage() {
        return this.isCanceled() ? DENY_DAMAGE : this.config.damage();
    }

    public double getDamage(double firstDamage) {
        return this.config.multiplier().preprocess(firstDamage + this.getDamage());
    }

    public boolean matches(Entity entity, EntityDamageEvent.DamageCause cause) {
        return !entity.isDead() || this.config.entityFilter().get().filter(entity, cause).isAllowed();
    }

    public boolean matches(GamePlayer player, EntityDamageEvent.DamageCause cause) {
        return player == null || !player.isOnline() || this.config.playerFilter().get().filter(player, cause).isAllowed();
    }

    public interface Config extends IConfig<DamageRule> {
        Ref<Filter> entityFilter();
        Ref<Filter> playerFilter();
        default double damage() { return DENY_DAMAGE; }
        default Percentage multiplier() { return Percentage.DONE; }
    }
}
