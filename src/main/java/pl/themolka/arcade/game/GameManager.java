package pl.themolka.arcade.game;

import org.bukkit.World;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.map.MapManager;
import pl.themolka.arcade.map.MapParser;
import pl.themolka.arcade.map.MapParserException;
import pl.themolka.arcade.map.MapQueue;
import pl.themolka.arcade.map.MapQueueFillEvent;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.session.ArcadePlayer;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.logging.Level;

public class GameManager {
    public static final int DEFAULT_MAX_GAME_ID = 15;

    private final ArcadePlugin plugin;

    private Game currentGame;
    private int gameId;
    private int maxGameId = DEFAULT_MAX_GAME_ID;
    private boolean nextRestart;
    private MapQueue queue = new MapQueue();

    public GameManager(ArcadePlugin plugin) {
        this.plugin = plugin;

        this.setDefaultMaxGameId();
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
                copiedFiles.append("[d]");
            } else if (file.isFile()) {
                copiedFiles.append("[f]");
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

            // refill queue if it's empty
            if (this.getQueue().hasNextMap()) {
                this.fillQueue();
            }
        }

        this.plugin.getLogger().info("Cycling to '" + target.getName() + "' from '" + target.getDirectory().getName() + "'...");
        try {
            Game game = this.createGame(target);

            if (this.currentGame != null) {
                this.destroyGame(this.getCurrentGame());

                this.gameId++;
                this.plugin.getServerSession().getContent().setLastGameId(this.getGameId());
            }

            this.setCurrentGame(game);

            if (this.getGameId() >= this.getMaxGameId()) {
                this.setNextRestart(true);
            }
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
        this.postEvent(event);

        if (!event.isCanceled()) {
            this.plugin.getServer().shutdown();
        }
    }

    public void destroyGame(Game game) {
        GameDestroyEvent worldEvent = new GameDestroyEvent(this.plugin, game);
        this.postEvent(worldEvent);

        game.stop();

        File directory = new File(this.plugin.getMaps().getWorldContainer(), game.getMap().getWorldName());
        this.serializeGame(new File(directory, Game.JSON_FILENAME), game);

        this.plugin.getMaps().destroyWorld(game.getWorld(), worldEvent.isSaveWorld());
        this.postEvent(new GameDestroyedEvent(this.plugin, game));
    }

    public void fillQueue() {
        Element queueElement = this.plugin.getSettings().getData().getChild("queue");
        if (queueElement == null) {
            queueElement = new Element("queue");
        }

        for (Element mapElement : queueElement.getChildren("map")) {
            String directory = mapElement.getAttributeValue("directory");
            String mapName = mapElement.getTextNormalize();

            OfflineMap map = null;
            if (directory != null) {
                map = this.plugin.getMaps().getContainer().getMapByDirectory(directory);
            } else if (mapName != null) {
                map = this.plugin.getMaps().getContainer().getMap(mapName);
            }

            if (map != null) {
                queue.addMap(map);
            }
        }

        this.postEvent(new MapQueueFillEvent(this.plugin, this.getQueue()));
    }

    public Game getCurrentGame() {
        return this.currentGame;
    }

    public int getGameId() {
        return this.gameId;
    }

    public int getMaxGameId() {
        return this.maxGameId;
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

    public void setDefaultMaxGameId() {
        Element queueElement = plugin.getSettings().getData().getChild("queue");
        if (queueElement == null) {
            return;
        }

        Attribute attribute = queueElement.getAttribute("restart-after");
        if (attribute == null) {
            return;
        }

        try {
            this.maxGameId = attribute.getIntValue();
        } catch (DataConversionException ignored) {
        }
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setMaxGameId(int maxGameId) {
        this.maxGameId = maxGameId;
    }

    public void setNextRestart(boolean nextRestart) {
        this.nextRestart = nextRestart;
    }

    public void serializeGame(File file, Game game) {
        try {
            this.plugin.serializeJsonFile(file, game);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void postEvent(Event event) {
        this.plugin.getEventBus().publish(event);
    }
}
