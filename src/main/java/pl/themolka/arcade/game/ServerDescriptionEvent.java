package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;

public class ServerDescriptionEvent extends GameEvent {
    private String description;

    public ServerDescriptionEvent(ArcadePlugin plugin, Game game) {
        super(plugin, game);
    }

    public String getDescription() {
        return this.description;
    }

    public boolean hasDescription() {
        return this.description != null;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
