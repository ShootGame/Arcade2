package pl.themolka.arcade.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.World;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.map.MapManager;
import pl.themolka.arcade.map.MapParser;
import pl.themolka.arcade.map.MapParserException;
import pl.themolka.arcade.map.MapQueue;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.session.ArcadePlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.logging.Level;

public class GameManager {
    private final ArcadePlugin plugin;

    private Game currentGame;
    private boolean nextRestart;
    private MapQueue queue = new MapQueue();
    private final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    public GameManager(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public Game createGame(ArcadeMap map) throws IOException {
        MapManager maps = this.plugin.getMaps();

        this.plugin.getLogger().info("Accessing the '" + map.getMapInfo().getDirectory().getName() + "' directory...");
        File[] copied = maps.copyFiles(map);

        StringBuilder copiedFiles = new StringBuilder();
        for (int i = 0; i < copied.length; i++) {
            File file = copied[i];
            copiedFiles.append(file.getName());

            if (file.isDirectory()) {
                copiedFiles.append(" [d]");
            } else if (file.isFile()) {
                copiedFiles.append(" [f]");
            }

            if (i != copied.length - 1) {
                copiedFiles.append(", ");
            }
        }

        this.plugin.getLogger().info("Copied " + copied.length + " map files - " + copiedFiles.toString() + ".");

        this.plugin.getLogger().info("Generating new unique world '" + map.getWorldName() + "' for map '" + map.getMapInfo().getName() + "'...");
        World world = maps.createWorld(map);

        Game game = new Game(this.plugin, map, world);
        map.setGame(game);

        map.getSpawn().setWorld(world);

        for (ArcadePlayer player : this.plugin.getPlayers()) {
            player.setGamePlayer(new GamePlayer(game, player));
        }
        game.start();

        this.resetPlayers(game);
        return game;
    }

    public Game createGame(OfflineMap map) throws IOException, MapParserException {
        MapParser parser = this.plugin.getMaps().getParser().newInstance();
        parser.readFile(map.getSettings());

        return this.createGame(parser.parseArcadeMap(map));
    }

    public void cycle(OfflineMap target) {
        Instant now = Instant.now();
        if (target == null) {
            OfflineMap next = this.getQueue().takeNextMap();
            if (next == null) {
                this.plugin.getLogger().severe("Map queue was empty");
                return;
            }

            target = next;
        }

        this.plugin.getLogger().info("Cycling to '" + target.getName() + "' from '" + target.getDirectory().getName() + "'...");
        try {
            Game game = this.createGame(target);

            if (this.currentGame != null) {
                this.destroyGame(this.getCurrentGame());
            }

            this.setCurrentGame(game);
        } catch (Throwable th) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not cycle to '" + target.getName() + "'", th);
            this.cycleNext();
            return;
        }

        this.plugin.getLogger().info("Cycled in '" + (Instant.now().toEpochMilli() - now.toEpochMilli()) + "' ms.");
    }

    public void cycleNext() {
        if (this.isNextRestart()) {
            this.cycleRestart();
        } else {
            this.cycle(null);
        }
    }

    public void cycleRestart() {
        CycleRestartEvent event = new CycleRestartEvent(this.plugin);
        this.plugin.getEvents().post(event);

        if (!event.isCanceled()) {
            this.plugin.getServer().shutdown();
        }
    }

    public void destroyGame(Game game) {
        GameDestroyEvent worldEvent = new GameDestroyEvent(this.plugin, game);
        this.plugin.getEvents().post(worldEvent);

        game.stop();

        File directory = new File(this.plugin.getMaps().getWorldContainer(), game.getMap().getWorldName());
        this.serializeGame(game, new File(directory, Game.JSON_FILENAME));

        this.plugin.getMaps().destroyWorld(game.getWorld(), worldEvent.isSaveWorld());
        this.plugin.getEvents().post(new GameDestroyedEvent(this.plugin, game));
    }

    public Game getCurrentGame() {
        return this.currentGame;
    }

    public MapQueue getQueue() {
        return this.queue;
    }

    public boolean isNextRestart() {
        return this.nextRestart;
    }

    public void resetPlayers(Game newGame) {
        if (this.getCurrentGame() != null) {
            for (GamePlayer player : this.getCurrentGame().getPlayers()) {
                player.reset();
                player.getPlayer().getBukkit().teleport(newGame.getMap().getSpawn());
            }
        }
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    public void setNextRestart(boolean nextRestart) {
        this.nextRestart = nextRestart;
    }

    public void serializeGame(Game game, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            this.gson.toJson(game, writer);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
