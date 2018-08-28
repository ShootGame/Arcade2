package pl.themolka.arcade.damage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DamageGame extends GameModule {
    private final Set<DamageRule> rules = new LinkedHashSet<>();

    public DamageGame() {
    }

    protected DamageGame(Game game, IGameConfig.Library library, Config config) {
        for (DamageRule.Config rule : config.rules().get()) {
            this.rules.add(library.getOrDefine(game, rule));
        }
    }

    public boolean addRule(DamageRule rule) {
        return this.rules.add(rule);
    }

    public List<DamageRule> getRules() {
        return new ArrayList<>(this.rules);
    }

    public boolean removeRule(DamageRule rule) {
        return this.rules.remove(rule);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        this.handleEntityDamage(event);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        this.handleEntityDamage(event);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        this.handleEntityDamage(event);
    }

    private void handleEntityDamage(EntityDamageEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (!isValid(cause)) {
            return;
        }

        Entity entity = event.getEntity();

        DamageRule rule;
        if (entity instanceof Player) {
            rule = this.byPlayer((Player) entity, cause);
        } else {
            rule = this.byEntity(entity, cause);
        }

        if (rule == null) {
            return;
        } else if (rule.isCanceled()) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(rule.getDamage(event.getDamage()));
    }

    private DamageRule byEntity(Entity entity, EntityDamageEvent.DamageCause cause) {
        for (DamageRule rule : this.rules) {
            if (rule.matches(entity, cause)) {
                return rule;
            }
        }

        return null;
    }

    private DamageRule byPlayer(Player bukkit, EntityDamageEvent.DamageCause cause) {
        GamePlayer player = this.getGame().resolve(bukkit);
        for (DamageRule rule : this.rules) {
            if (rule.matches(player, cause)) {
                return rule;
            }
        }

        return null;
    }

    public static boolean isValid(EntityDamageEvent.DamageCause cause) {
        return !cause.equals(EntityDamageEvent.DamageCause.VOID);
    }

    public interface Config extends IGameModuleConfig<DamageGame> {
        Ref<Set<DamageRule.Config>> rules();

        @Override
        default DamageGame create(Game game, Library library) {
            return new DamageGame(game, library, this);
        }
    }
}
