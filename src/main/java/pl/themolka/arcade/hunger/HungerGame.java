package pl.themolka.arcade.hunger;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;

public class HungerGame extends GameModule {
    private final Filter filter;

    protected HungerGame(Game game, IGameConfig.Library library, Config config) {
        this.filter = Filters.secure(library.getOrDefine(game, config.filter().getIfPresent()));
    }

    public boolean cannotDeplete(GamePlayer player) {
        return player == null || !player.isOnline() || this.filter.filter(player).isNotFalse();
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
        default Ref<Filter.Config<?>> filter() { return Ref.empty(); }

        @Override
        default HungerGame create(Game game, Library library) {
            return new HungerGame(game, library, this);
        }
    }
}
