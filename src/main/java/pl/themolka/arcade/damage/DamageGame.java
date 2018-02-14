package pl.themolka.arcade.damage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jdom2.Element;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

public class DamageGame extends GameModule {
    private final List<DamageRule> rules = new ArrayList<>();

    @Override
    public void onEnable() {
        FiltersGame filters = (FiltersGame) this.getGame().getModule(FiltersModule.class);
        if (filters != null) {
            for (Element xml : this.getSettings().getChildren("rule")) {
                String global = xml.getAttributeValue("filter");
                String entity = xml.getAttributeValue("entity-filter");
                String player = xml.getAttributeValue("player-filter");

                if (global != null) {
                    if (entity == null) {
                        entity = global;
                    }
                    if (player == null) {
                        player = global;
                    }
                }

                Filter entityFilter = filters.filterOrDefault(entity, null);
                Filter playerFilter = filters.filterOrDefault(player, null);

                if (entityFilter == null || playerFilter == null) {
                    continue;
                }

                DamageRule rule = new DamageRule(entityFilter, playerFilter);

                String value = xml.getValue();
                boolean deny = XMLParser.parseBoolean(value, false);
                double damage = XMLParser.parseDouble(value, -1D);

                if (deny || damage == DamageRule.ZERO) {
                    rule.setCanceled(true);
                } else {
                    if (damage < DamageRule.ZERO) {
                        continue;
                    }

                    rule.setDamage(damage);
                }

                this.rules.add(rule);
            }
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
        this.handleEntityDamage(event, null);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        this.handleEntityDamage(event, event.getDamager());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        this.handleEntityDamage(event, event.getDamager());
    }

    private void handleEntityDamage(EntityDamageEvent event, Object damager) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (!isValid(cause)) {
            return;
        }

        Entity entity = event.getEntity();

        DamageRule rule;
        if (entity instanceof Player) {
            rule = this.byPlayer((Player) entity, cause, null);
        } else {
            rule = this.byEntity(entity, cause, null);
        }

        if (rule == null) {
            return;
        } else if (rule.isCanceled()) {
            event.setCancelled(true);
        }

        event.setDamage(rule.getDamage());
    }

    private DamageRule byEntity(Entity entity, EntityDamageEvent.DamageCause cause, Object damager) {
        for (DamageRule rule : this.rules) {
            if (rule.matches(entity, cause, damager)) {
                return rule;
            }
        }

        return null;
    }

    private DamageRule byPlayer(Player bukkit, EntityDamageEvent.DamageCause cause, Object damager) {
        GamePlayer player = this.getGame().getPlayer(bukkit);
        for (DamageRule rule : this.rules) {
            if (rule.matches(player, cause, damager)) {
                return rule;
            }
        }

        return null;
    }

    public static boolean isValid(EntityDamageEvent.DamageCause cause) {
        return !cause.equals(EntityDamageEvent.DamageCause.VOID);
    }
}
