package pl.themolka.arcade.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamePlayerManager {
    /** Storing online players. */
    private final Map<UUID, GamePlayer> online = new HashMap<>();
    /** Storing all players ever joined. */
    private final Map<UUID, GamePlayer> players = new HashMap<>();

    public Collection<GamePlayer> getAllPlayers() {
        return this.players.values();
    }

    public Collection<GamePlayer> getOnlinePlayers() {
        return this.online.values();
    }

    public GamePlayer getPlayer(UUID uniqueId) {
        return uniqueId != null ? this.players.get(uniqueId) : null;
    }

    public void playerJoin(GamePlayer join) {
        this.players.put(join.getUuid(), join);
        this.online.put(join.getUuid(), join);
    }

    public boolean playerQuit(GamePlayer quit) {
        return this.playerQuit(quit.getUuid()) != null;
    }

    public GamePlayer playerQuit(UUID quit) {
        return this.online.remove(quit);
    }

    public boolean unregister(GamePlayer unregister) {
        return this.unregister(unregister.getUuid()) != null;
    }

    public GamePlayer unregister(UUID uniqueId) {
        this.playerQuit(uniqueId);
        return this.players.remove(uniqueId);
    }
}
