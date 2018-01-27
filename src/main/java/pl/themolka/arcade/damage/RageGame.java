package pl.themolka.arcade.damage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.themolka.arcade.game.GameModule;

public class RageGame extends GameModule {
    private final double damage;

    public RageGame(double damage) {
        this.damage = damage;
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
}
