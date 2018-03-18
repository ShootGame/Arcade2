package pl.themolka.arcade.hunger;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameModuleConfig;

public class HungerGame extends GameModule {
    private Filter filter;

    @Deprecated
    public HungerGame() {
        this.filter = Filters.undefined();
    }

    protected HungerGame(Config config) {
        this.filter = Filters.secure(config.filter().getIfPresent());
    }

    @Override
    public void onEnable() {
        FiltersGame filters = (FiltersGame) this.getGame().getModule(FiltersModule.class);
        if (filters != null) {
            String global = this.getSettings().getAttributeValue("filter");

            this.filter = filters.filterOrDefault(global, this.filter);
        }
    }

    public boolean cannotDeplete(GamePlayer player) {
        return player == null || !player.isOnline() || this.getFilter().filter(player).isDenied();
    }

    public Filter getFilter() {
        return this.filter;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        HumanEntity entity = event.getEntity();
        if (entity instanceof Player && this.cannotDeplete(this.getGame().getPlayer((Player) entity))) {
            event.setCancelled(true);
            event.setFoodLevel(((Player) entity).getFoodLevel());
        }
    }

    public interface Config extends IGameModuleConfig<HungerGame> {
        default Ref<Filter> filter() { return Ref.empty(); }

        @Override
        default HungerGame create(Game game) {
            return new HungerGame(this);
        }
    }
}
