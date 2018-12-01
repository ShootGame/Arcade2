/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.game;

import net.minecraft.server.PlayerList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.config.ConfigContext;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.config.RefContainer;
import pl.themolka.arcade.config.RefFinder;
import pl.themolka.arcade.cycle.CycleCountdown;
import pl.themolka.arcade.cycle.CycleRestartEvent;
import pl.themolka.arcade.cycle.ServerCycleEvent;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.dom.engine.DOMEngine;
import pl.themolka.arcade.event.Event;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.map.MapManager;
import pl.themolka.arcade.map.MapManifest;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.map.WorldNameGenerator;
import pl.themolka.arcade.map.queue.MapQueue;
import pl.themolka.arcade.map.queue.MapQueueFillEvent;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.settings.Settings;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Tickable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SimpleGameManager implements GameManager {
    public static final int DEFAULT_MAX_GAME_ID = 15;

    private final ArcadePlugin plugin;

    private Game currentGame;
    private final CycleCountdown cycleCountdown;
    private int gameId = 0;
    private int maxGameId = DEFAULT_MAX_GAME_ID;
    private boolean nextRestart;
    private final RefFinder refFinder = new RefFinder();
    private MapQueue queue = new MapQueue();
    private final RestartCountdown restartCountdown;
    private final WorldNameGenerator worldNameGenerator = new WorldNameGenerator();

    public SimpleGameManager(ArcadePlugin plugin) {
        this.plugin = plugin;

        this.cycleCountdown = new CycleCountdown(plugin, this.readCycleCountdown(plugin.getSettings()));
        this.restartCountdown = new RestartCountdown(plugin, this.readCycleCountdown(plugin.getSettings()));
        this.setDefaultMaxGameId();
    }

    @Override
    public Game createGame(ArcadeMap map) throws DOMException, IOException {
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

        Game game = new Game(this.plugin, this.gameId++, map, world);
        map.setGame(game);

        // Don't forget to setup the world object!
        map.getManifest().getWorld().getSpawn().setWorld(world);

        GamePlayerManager playerManager = game.getPlayers();
        for (ArcadePlayer player : this.plugin.getPlayers()) {
            GamePlayer gamePlayer = player.getGamePlayer();
            gamePlayer.getBossBarFacet().removeAll(); // this should be moved to a service

            player.setGamePlayer(new GamePlayer(game, player));
            playerManager.playerJoin(player.getGamePlayer());
        }

        this.resetPlayers(game);
        return game;
    }

    @Override
    public Game createGame(OfflineMap map) throws DOMException, IOException {
        File file = map.getSettings();
        DOMEngine engine = this.plugin.getDomEngines().forFile(file);

        Parser<MapManifest> parser;
        try {
            parser = this.plugin.getParsers().forType(MapManifest.class);
        } catch (ParserNotSupportedException ex) {
            throw new RuntimeException("No " + MapManifest.class.getSimpleName() + " parser installed");
        }

        Document document = engine.read(file);
        this.plugin.getDomPreprocessor().preprocess(document);

        MapManifest manifest = parser.parse(document).orFail();

        ConfigContext config = new ConfigContext(this.refFinder);

        List<Ref<?>> refs;
        try {
            refs = config.getRefFinder().find((Iterable) manifest.getModules());
            System.out.println(StringUtils.join(refs, ", "));
        } catch (InvocationTargetException e) {
            throw new DOMException(null, e);
        }

        RefContainer container = new RefContainer();
        container.registerMany(refs);

        for (Ref<?> ref : container) {
            if (ref.isProvided()) {
                continue;
            }

            String id = ref.getId();
            Ref<?> value = container.query(id);

            // ?
        }

        ArcadeMap realMap = new ArcadeMap(map, manifest, config);

        // ----- TODO -----
        // hook references, fail if the specified ID does not exist
        // create ConfigConfig with all the configs and store it somewhere
        // create IGameConfig.Library and create module configs, catch all NPEs
        // enable the modules :D

        realMap.setWorldName(this.worldNameGenerator.nextWorldName());
        return this.createGame(realMap);
    }

    @Override
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
            if (!this.getQueue().hasNextMap()) {
                this.fillDefaultQueue();
            }
        }

        this.plugin.getLogger().info("Cycling to '" + target.getName() + "' from the '" + target.getDirectory().getName() + "' directory...");
        try {
            Game oldGame = this.getCurrentGame();
            Game game = this.createGame(target);
            this.plugin.getEventBus().publish(new ServerCycleEvent(this.plugin, game, oldGame));

            if (this.currentGame != null) {
                this.destroyGame(this.getCurrentGame());
            }

            this.setCurrentGame(game);
            game.start();

            if (this.getGameId() >= this.getMaxGameId()) {
                this.setNextRestart(true);
            }
        } catch (DOMException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not cycle to '" + target.getName() + "': " + ex.toString());

            this.cycleNext();
            return;
        } catch (Throwable th) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not cycle to '" + target.getName() + "'", th);

            this.cycleNext();
            return;
        }

        this.plugin.getLogger().info("Cycled in " + (Instant.now().toEpochMilli() - now.toEpochMilli()) + " ms.");
    }

    @Override
    public void cycleRestart() {
        CycleRestartEvent event = new CycleRestartEvent(this.plugin);
        this.postEvent(event);

        if (!event.isCanceled()) {
            this.plugin.getLogger().info("Restarting " + this.plugin.getServerName() + "...");

            String reason = ChatColor.RED + this.plugin.getServerName() + " is now restarting...";
            for (Player online : new ArrayList<>(this.plugin.getServer().getOnlinePlayers())) {
                online.kickPlayer(reason);
            }

            // We need to shutdown the server in the next tick because of a
            // strange "handleDisconnection() called twice" log from the server.
            this.plugin.addTickable(new Tickable() {
                @Override
                public void onTick(long tick) {
                    plugin.removeTickable(this); // Don't tick again.
                    plugin.getServer().shutdown();
                }
            });
        }
    }

    @Override
    public void destroyGame(Game game) {
        GameDestroyEvent worldEvent = new GameDestroyEvent(this.plugin, game);
        this.postEvent(worldEvent);

        game.stop();

        File directory = new File(this.plugin.getMaps().getWorldContainer(), game.getMap().getWorldName());
        this.plugin.getMaps().destroyWorld(game.getWorld(), worldEvent.isSaveWorld());

        GameDestroyedEvent destroyedEvent = new GameDestroyedEvent(this.plugin, game);
        this.postEvent(destroyedEvent);

        if (!destroyedEvent.isSaveDirectory()) {
            FileUtils.deleteQuietly(directory);
        }
    }

    @Override
    public void fillDefaultQueue() {
        Node node = this.plugin.getSettings().getData().child("queue");
        if (node == null) {
            node = Node.empty();
        }

        for (Node mapNode : node.children("map")) {
            String directory = mapNode.propertyValue("directory");
            String mapName = mapNode.getValue();

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

    @Override
    public Game getCurrentGame() {
        return this.currentGame;
    }

    @Override
    public CycleCountdown getCycleCountdown() {
        return this.cycleCountdown;
    }

    @Override
    public int getGameId() {
        return this.gameId;
    }

    @Override
    public int getMaxGameId() {
        return this.maxGameId;
    }

    @Override
    public MapQueue getQueue() {
        return this.queue;
    }

    @Override
    public RestartCountdown getRestartCountdown() {
        return this.restartCountdown;
    }

    @Override
    public boolean isNextRestart() {
        return this.nextRestart;
    }

    @Override
    public void resetPlayers(Game newGame) {
        PlayerList playerList = ((CraftServer) newGame.getServer()).getHandle();
        int worldId = ((CraftWorld) newGame.getWorld()).getHandle().dimension;
        Location spawn = newGame.getMap().getManifest().getWorld().getSpawn();

        for (GamePlayer player : newGame.getPlayers().getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            // We have to force player to respawn. The only way to do it is to
            // teleport the player between worlds. Bukkit's Entity.teleport()
            // will not teleport the player it is in the dead state. We must use
            // NMS to teleport the player (and skip firing PlayerTeleportEvent).
            playerList.moveToWorld(player.getMojang(), worldId, false, spawn, false);
            player.reset(); // setHealth may break something in the moveToWorld
        }
    }

    @Override
    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;

        this.getCycleCountdown().setGame(currentGame);
        this.getRestartCountdown().setGame(currentGame);
    }

    @Override
    public void setDefaultMaxGameId() {
        Node node = this.plugin.getSettings().getData().child("queue");
        if (node == null) {
            return;
        }

        try {
            ParserContext context = this.plugin.getParsers().createContext();
            this.setMaxGameId(context.type(Integer.class).parse(node.property("restart-after")).orFail());
        } catch (ParserNotSupportedException | ParserException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public void setMaxGameId(int maxGameId) {
        this.maxGameId = maxGameId;
    }

    @Override
    public void setNextRestart(boolean nextRestart) {
        this.nextRestart = nextRestart;
    }

    private void postEvent(Event event) {
        this.plugin.getEventBus().publish(event);
    }

    private Duration readCycleCountdown(Settings settings) {
        Node cycle = settings.getData().child("cycle");

        if (cycle != null) {
            Property countdown = cycle.property("countdown");
            if (countdown != null) {
                Time time = Time.parseTime(countdown.getValue());
                if (time != null) {
                    return time.toDuration();
                }
            }
        }

        return CycleCountdown.DEFAULT_DURATION;
    }
}
