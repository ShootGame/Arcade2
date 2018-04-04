package pl.themolka.arcade.mob;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jdom2.Element;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.filter.matcher.SpawnReasonMatcher;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class MobsGame extends GameModule {
    private final List<MobSpawnRule> rules = new ArrayList<>();
    private final boolean denyNatural;

    @Deprecated
    public MobsGame(boolean denyNatural) {
        this.denyNatural = denyNatural;
    }

    protected MobsGame(Game game, Config config) {
        for (MobSpawnRule.Config ruleConfig : config.rules()) {
            this.rules.add(ruleConfig.create(game));
        }

        this.denyNatural = config.denyNatural();
    }

    @Override
    public void onEnable() {
        Game game = this.getGame();
        if (this.denyNatural) {
            // Register the natural rule first, so it is handled before any other rules.

            Filter naturalSpawnFilter = new SpawnReasonMatcher.Config() {
                public Ref<SpawnReason> value() { return Ref.ofProvided(SpawnReason.NATURAL); }
            }.create(game);

            MobSpawnRule naturalSpawnRule = new MobSpawnRule.Config() {
                public Ref<Filter> filter() { return Ref.ofProvided(naturalSpawnFilter); }
                public boolean cancel() { return true; }
            }.create(game);

            this.rules.add(naturalSpawnRule);
        }

        FiltersGame filters = (FiltersGame) game.getModule(FiltersModule.class);
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

    public interface Config extends IGameModuleConfig<MobsGame> {
        default List<MobSpawnRule.Config> rules() { return Collections.emptyList(); }
        default boolean denyNatural() { return false; }

        @Override
        default MobsGame create(Game game) {
            return new MobsGame(game, this);
        }
    }
}
