package pl.themolka.arcade.damage;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.themolka.arcade.attribute.BukkitAttributeKey;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;

public class RageGame extends GameModule {
    private static final BukkitAttributeKey MAX_HEALTH = new BukkitAttributeKey(Attribute.GENERIC_MAX_HEALTH);
    private static final double DEFAULT_MAX_HEALTH = 1_000_000D;

    private final Filter filter;

    protected RageGame(Game game, IGameConfig.Library library, Config config) {
        this.filter = Filters.secure(library.getOrDefine(game, config.filter().getIfPresent()));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();

        if (victim instanceof LivingEntity && this.filter.filter(victim, damager).isNotFalse()) {
            if (damager instanceof Player) {
                this.damage(event, (LivingEntity) victim);
            } else if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player) {
                this.damage(event, (LivingEntity) victim);
            }
        }
    }

    private void damage(EntityDamageByEntityEvent event, LivingEntity victim) {
        AttributeInstance maxHealth = victim.getAttribute(MAX_HEALTH.getBukkit());
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, maxHealth != null ? maxHealth.getValue()
                                                                                 : DEFAULT_MAX_HEALTH);
    }

    public interface Config extends IGameModuleConfig<RageGame> {
        default Ref<Filter.Config<?>> filter() { return Ref.empty(); }

        @Override
        default RageGame create(Game game, Library library) {
            return new RageGame(game, library, this);
        }
    }
}
