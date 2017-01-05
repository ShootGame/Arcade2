package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;

public class GameDestroyedEvent extends GameEvent {
    private boolean saveDirectory;

    public GameDestroyedEvent(ArcadePlugin plugin, Game game) {
        super(plugin, game);
    }

    public boolean isSaveDirectory() {
        return this.saveDirectory;
    }

    public void setSaveDirectory(boolean saveDirectory) {
        this.saveDirectory = saveDirectory;
    }
}
