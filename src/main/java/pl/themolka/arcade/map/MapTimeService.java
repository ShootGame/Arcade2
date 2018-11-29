package pl.themolka.arcade.map;

import net.engio.mbassy.listener.Handler;
import org.bukkit.World;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameStartEvent;
import pl.themolka.arcade.gamerule.GameRuleType;
import pl.themolka.arcade.service.Service;
import pl.themolka.arcade.service.ServiceId;

/**
 * The core plugin can set time in world. Make it happend on the {@link World}
 * object too.
 */
@ServiceId("MapTime")
public class MapTimeService extends Service {
    private static final GameRuleType doDaylightCycle = GameRuleType.DO_DAYLIGHT_CYCLE;

    @Handler(priority = Priority.HIGHEST)
    public void onGameStart(GameStartEvent event) {
        Game game = event.getGame();
        MapTime time = game.getMap().getManifest().getWorld().getTime();

        this.walkTime(game.getWorld(), time.getTicks(), time.isLocked());
    }

    private void walkTime(World world, long time, boolean lock) {
        world.setFullTime(time);

        if (lock) {
            String oldValue = world.getGameRuleValue(doDaylightCycle.getKey());
            String correctValue = Boolean.toString(false);

            if (oldValue == null || !oldValue.equalsIgnoreCase(correctValue)) {
                world.setGameRuleValue(doDaylightCycle.getKey(), correctValue);
            }
        }
    }
}
