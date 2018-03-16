package pl.themolka.arcade.life;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchStartedEvent;
import pl.themolka.arcade.respawn.PlayerRespawnEvent;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.PlayerQuitEvent;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class LivesGame extends GameModule {
    public static final Sound DEFAULT_SOUND = Sound.ENTITY_IRONGOLEM_DEATH;

    public static final int DEATH_REVOKE = -1;
    public static final int ZERO = 0;

    private Match match;

    private final int lives;
    private final boolean broadcast;
    private final Sound sound;

    private final Map<GamePlayer, Integer> remaining = new WeakHashMap<>();

    @Deprecated
    public LivesGame(int lives, boolean broadcast, Sound sound) {
        this.lives = lives;
        this.broadcast = broadcast;
        this.sound = sound;
    }

    protected LivesGame(Config config) {
        this.lives = config.lives();
        this.broadcast = config.broadcast();
        this.sound = config.sound();
    }

    @Override
    public void onEnable() {
        this.match = ((MatchGame) this.getGame().getModule(MatchModule.class)).getMatch();
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        if (this.broadcast()) {
            register.add(new BroadcastListener());
        }

        if (this.sound() != null) {
            register.add(new SoundListener());
        }

        return register;
    }

    public int lives() {
        return this.lives;
    }

    public boolean broadcast() {
        return this.broadcast;
    }

    public Sound sound() {
        return this.sound;
    }

    //
    // Lives
    //

    public int addLives(GamePlayer player, int lives) {
        int newValue = this.calculate(player, lives);

        if (newValue > ZERO) {
            // save the newValue if it is positive
            this.remaining.put(player, newValue);
        } else {
            // eliminate the player if the newValue is zero or negative
            this.eliminate(player, false);
        }

        return newValue;
    }

    public int calculate(GamePlayer player, int lives) {
        return remaining(player) + lives;
    }

    public int remaining(GamePlayer player) {
        return this.remaining.getOrDefault(player, this.lives());
    }

    public void eliminate(GamePlayer player, boolean serverQuit) {
        if (this.remaining.containsKey(player)) {
            PlayerEliminateEvent event = new PlayerEliminateEvent(this.getPlugin(),
                    player, this.remaining.getOrDefault(player, ZERO), player.getBukkit().getLocation());
            this.getPlugin().getEventBus().publish(event);

            if (event.isCanceled() && !serverQuit) {
                this.remaining.put(event.getPlayer(), event.getRemainingLives());
                return;
            }

            this.remaining.remove(event.getPlayer());
            if (!serverQuit) {
                this.match.getObservers().join(event.getPlayer(), true);
            }

            // I don't know if it should be done here.
            this.match.refreshWinners();
        }
    }

    @Handler(priority = Priority.HIGH)
    public void revokeLife(PlayerDeathEvent event) {
        if (event.hasDeathMessage() && event.getVictim().isParticipating()) {
            this.addLives(event.getVictim(), DEATH_REVOKE);
        }
    }

    @Handler(priority = Priority.LAST)
    public void revokePlayer(PlayerQuitEvent event) {
        this.eliminate(event.getGamePlayer(), true);
    }

    @Handler(priority = Priority.LAST)
    public void fillMap(MatchStartedEvent event) {
        for (ArcadePlayer player : event.getPlugin().getPlayers()) {
            this.remaining.put(player.getGamePlayer(), this.lives());
        }
    }

    //
    // Broadcasts
    //

    private class BroadcastListener {
        @Handler(priority = Priority.LAST)
        public void eliminate(PlayerEliminateEvent event) {
            // This listener is called BEFORE the actual elimination logic.
            if (calculate(event.getPlayer(), DEATH_REVOKE) <= ZERO) {
                event.getPlayer().send(this.createText(ZERO));
            }
        }

        @Handler(priority = Priority.LAST)
        public void respawn(PlayerRespawnEvent event) {
            GamePlayer player = event.getGamePlayer();
            if (player.isParticipating()) {
                player.send(this.createText(remaining(player)));
            }
        }

        @Handler(priority = Priority.LAST)
        public void start(MatchStartedEvent event) {
            for (ArcadePlayer online : event.getPlugin().getPlayers()) {
                GamePlayer player = online.getGamePlayer();
                if (player != null && player.isParticipating()) {
                    player.send(this.createText(remaining(player)));
                }
            }
        }

        String createText(int lives) {
            return Messageable.SUCCESS_COLOR + "You have " + ChatColor.GRAY + ChatColor.BOLD + lives + " " +
                    (lives == 1 ? "life" : "lives") + ChatColor.RESET + Messageable.SUCCESS_COLOR + " remaining.";
        }
    }

    //
    // Sounds
    //

    private class SoundListener {
        @Handler(priority = Priority.LAST)
        public void eliminate(PlayerEliminateEvent event) {
            this.play(sound(), event.getDeathLocation());
        }

        void play(Sound sound, Location at) {
            at.getWorld().playSound(at, sound, 1F, 1F);
        }
    }

    public interface Config extends IGameModuleConfig<LivesGame> {
        default int lives() { return 1; }
        default boolean broadcast() { return true; }
        default Sound sound() { return DEFAULT_SOUND; }

        @Override
        default LivesGame create(Game game) {
            return new LivesGame(this);
        }
    }
}
