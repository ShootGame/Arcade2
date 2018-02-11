package pl.themolka.arcade.listener;

import net.engio.mbassy.listener.Handler;
import org.bukkit.World;
import org.bukkit.event.Listener;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameStartEvent;
import pl.themolka.arcade.gamerule.GameRuleType;
import pl.themolka.arcade.map.MapTime;

/**
 * Applying defined time options into the starting game's world.
 */
public class TimeListeners implements Listener {
    private static final String doDaylightCycle = GameRuleType.DO_DAYLIGHT_CYCLE.getKey();

    private final ArcadePlugin plugin;

    public TimeListeners(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Handler(priority = Priority.HIGHEST)
    public void onGameStart(GameStartEvent event) {
        Game game = event.getGame();
        MapTime time = game.getMap().getTime();

        World world = game.getWorld();
        world.setFullTime(time.getTicks());

        if (time.isLocked()) {
            this.lock(world);
        }
    }

    private void lock(World world) {
        String oldValue = world.getGameRuleValue(doDaylightCycle);
        String correctValue = Boolean.TRUE.toString();

        if (oldValue == null || !oldValue.equalsIgnoreCase(correctValue)) {
            world.setGameRuleValue(doDaylightCycle, correctValue);
        }
    }
}
