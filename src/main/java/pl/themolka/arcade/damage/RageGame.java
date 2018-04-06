package pl.themolka.arcade.damage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameModuleConfig;

public class RageGame extends GameModule {
    private final double damage;

    protected RageGame(Config config) {
        this.damage = config.damage().getOrDefault(Config.DEFAULT_DAMAGE);
    }

    public double getDamage() {
        return this.damage;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            this.damage(event);
        } else if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player) {
            this.damage(event);
        }
    }

    private void damage(EntityDamageByEntityEvent event) {
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, this.getDamage());
    }

    public interface Config extends IGameModuleConfig<RageGame> {
        double DEFAULT_DAMAGE = 10_000D;

        default Ref<Double> damage() { return Ref.empty(); }

        @Override
        default RageGame create(Game game, Library library) {
            return new RageGame(this);
        }
    }
}
