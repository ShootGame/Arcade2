package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.util.Tickable;

public class DescriptionTickable implements Tickable {
    private final ArcadePlugin plugin;

    public DescriptionTickable(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onTick(long tick) {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            return;
        }

        String description = this.getDescription(game);
        if (description != null) {
            this.plugin.setServerDescription(description);
        }
    }

    private String getDescription(Game game) {
        ServerDescriptionEvent event = new ServerDescriptionEvent(this.plugin, game);
        this.plugin.getEventBus().publish(event);

        return event.getDescription();
    }
}
