package pl.themolka.arcade.mob;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jdom2.Element;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.filter.matcher.SpawnReasonMatcher;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class MobsGame extends GameModule {
    private final List<MobSpawnRule> rules = new ArrayList<>();
    private final boolean denyNatural;

    public MobsGame(boolean denyNatural) {
        this.denyNatural = denyNatural;
    }

    @Override
    public void onEnable() {
        if (this.denyNatural) {
            // Register the natural rule first, so it
            // is handled before any other rules.
            this.rules.add(new MobSpawnRule(new SpawnReasonMatcher(SpawnReason.NATURAL), true));
        }

        FiltersGame filters = (FiltersGame) this.getGame().getModule(FiltersModule.class);
        for (Element xml : this.getSettings().getChildren("rule")) {
            Filter filter = filters.filterOrDefault(xml.getAttributeValue("filter"), null);
            if (filter != null) {
                this.rules.add(new MobSpawnRule(filter, XMLParser.parseBoolean(xml.getValue(), false)));
            }
        }
    }

    public boolean addRule(MobSpawnRule rule) {
        return this.rules.add(rule);
    }

    public List<MobSpawnRule> getRules() {
        return new ArrayList<>(this.rules);
    }

    public boolean removeRule(MobSpawnRule rule) {
        return this.rules.remove(rule);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onMobSpawn(CreatureSpawnEvent event) {
        MobSpawnRule rule = this.findRule(event.getEntity(), event.getSpawnReason(), event.getLocation());
        if (rule != null && rule.isCanceled()) {
            event.setCancelled(true);
        }
    }

    private MobSpawnRule findRule(Entity entity, SpawnReason reason, Location location) {
        for (MobSpawnRule rule : this.rules) {
            if (rule.matches(entity, reason, location)) {
                return rule;
            }
        }

        return null;
    }
}
