package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;

public class GameDestroyEvent extends GameEvent {
    private boolean saveWorld;

    public GameDestroyEvent(ArcadePlugin plugin, Game game) {
        super(plugin, game);
    }

    public boolean isSaveWorld() {
        return this.saveWorld;
    }

    public void setSaveWorld(boolean saveWorld) {
        this.saveWorld = saveWorld;
    }
}
