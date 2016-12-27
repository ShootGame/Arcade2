package pl.themolka.arcade.game;

import org.bukkit.World;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.map.MapManager;
import pl.themolka.arcade.map.MapParser;
import pl.themolka.arcade.map.MapParserException;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.session.ArcadePlayer;

import java.io.IOException;
import java.time.Instant;
import java.util.logging.Level;

public class GameManager {
    private final ArcadePlugin plugin;

    private Game currentGame;

    public GameManager(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public Game createGame(ArcadeMap map) throws IOException {
        MapManager maps = this.plugin.getMaps();

        plugin.getLogger().info("Accessing the '" + map.getMapInfo().getDirectory().getName() + "' directory...");
        maps.copyFiles(map.getMapInfo());

        plugin.getLogger().info("Generating new world for map '" + map.getMapInfo().getName() + "'...");
        World world = maps.createWorld(map);

        Game game = new Game(this.plugin, map, world);
        game.start();

        this.resetPlayers(game);
        return game;
    }

    public Game createGame(OfflineMap map) throws IOException, MapParserException {
        MapParser parser = this.plugin.getMaps().getParser().newInstance();
        return this.createGame(parser.parseArcadeMap(map));
    }

    public void cycle(OfflineMap target) {
        Instant now = Instant.now();
        if (target == null) {
            // TODO queue
        }

        plugin.getLogger().info("Cycling to '" + target.getName() + "' from '" + target.getDirectory().getName() + "'...");
        try {
            Game game = this.createGame(target);

            if (this.currentGame != null) {
                this.destroyGame(this.getCurrentGame());
            }

            this.setCurrentGame(game);
        } catch (Throwable th) {
            plugin.getLogger().log(Level.SEVERE, "Could not cycle to '" + target.getName() + "'", th);
            return;
        }

        plugin.getLogger().info("Cycled in '" + (Instant.now().toEpochMilli() - Instant.now().toEpochMilli()) + "' ms.");
    }

    public void cycleNext() {
        this.cycle(null);
    }

    public void destroyGame(Game game) {
        game.stop();

        GameDestroyEvent worldEvent = new GameDestroyEvent(this.plugin, game);
        this.plugin.getEvents().post(worldEvent);

        this.plugin.getMaps().destroyWorld(game.getWorld(), worldEvent.isSaveWorld());
        this.plugin.getEvents().post(new GameDestroyedEvent(this.plugin, game));
    }

    public Game getCurrentGame() {
        return this.currentGame;
    }

    public void resetPlayers(Game newGame) {
        for (ArcadePlayer player : this.plugin.getPlayers()) {
            player.reset();
            player.getBukkit().teleport(newGame.getMap().getSpawn());
        }
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }
}
