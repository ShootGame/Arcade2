package pl.themolka.arcade.mob;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.matcher.SpawnReasonMatcher;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class MobsGame extends GameModule {
    private final List<MobSpawnRule> rules = new ArrayList<>();
    private final boolean denyNatural;

    protected MobsGame(Game game, IGameConfig.Library library, Config config) {
        for (MobSpawnRule.Config rule : config.rules().get()) {
            this.rules.add(library.getOrDefine(game, rule));
        }

        if (this.denyNatural = config.denyNatural().get()) {
            Filter.Config<?> filter = new SpawnReasonMatcher.Config() {
                public Ref<SpawnReason> value() { return Ref.ofProvided(SpawnReason.NATURAL); }
            };

            this.rules.add(library.getOrDefine(game, new MobSpawnRule.Config() {
                public Ref<Filter.Config<?>> filter() { return Ref.ofProvided(filter); }
                public Ref<Boolean> cancel() { return Ref.ofProvided(true); }
            }));
        }
    }

    public boolean addRule(MobSpawnRule rule) {
        return this.rules.add(rule);
    }

    public boolean denyNatural() {
        return this.denyNatural;
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

    public interface Config extends IGameModuleConfig<MobsGame> {
        default Ref<List<MobSpawnRule.Config>> rules() { return Ref.ofProvided(Collections.emptyList()); }
        default Ref<Boolean> denyNatural() { return Ref.ofProvided(false); }

        @Override
        default MobsGame create(Game game, Library library) {
            return new MobsGame(game, library, this);
        }
    }
}
