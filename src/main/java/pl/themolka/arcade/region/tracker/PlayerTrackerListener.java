package pl.themolka.arcade.region.tracker;

import org.bukkit.Location;
import pl.themolka.arcade.game.GamePlayer;

public interface PlayerTrackerListener {
    void onEnter(GamePlayer player, Location enter);

    void onLeave(GamePlayer player, Location leave);
}
