package pl.themolka.arcade.life;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.respawn.PlayerRespawnEvent;
import pl.themolka.arcade.session.PlayerQuitEvent;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class LivesGame extends GameModule {
    public static final int DEATH_REVOKE = -1;

    private final Match match;

    private final int lives;
    private final boolean broadcast;
    private final Sound sound;

    private final Map<GamePlayer, Integer> remaining = new WeakHashMap<>();

    public LivesGame(Match match, int lives, boolean broadcast, Sound sound) {
        this.match = match;

        this.lives = lives;
        this.broadcast = broadcast;
        this.sound = sound;
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

    public int addLives(GamePlayer player, int lives) {
        int newValue = this.remaining(player) + lives;

        this.remaining.put(player, newValue);
        return newValue;
    }

    public int remaining(GamePlayer player) {
        return this.remaining.getOrDefault(player, this.lives());
    }

    public void eliminate(GamePlayer player) {
        if (this.remaining.remove(player) != null) {
            this.getPlugin().getEventBus().publish(new PlayerEliminateEvent(this.getPlugin(), player));

            this.match.getObservers().joinForce(player);
            this.match.refreshWinners();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void revokeLife(PlayerDeathEvent event) {
        if (event.getDeathMessage() == null) {
            return;
        }

        GamePlayer player = this.getGame().getPlayer(event.getEntity());
        if (player == null || !player.isParticipating()) {
            return;
        }

        int remaining = this.addLives(player, DEATH_REVOKE);

        if (remaining <= 0) {
            this.eliminate(player);
        }
    }

    @Handler(priority = Priority.LOW)
    public void revokePlayer(PlayerQuitEvent event) {
        this.eliminate(event.getGamePlayer());
    }

    private class BroadcastListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void eliminate(PlayerDeathEvent event) {
            GamePlayer player = getGame().getPlayer(event.getEntity());
            if (player != null && match.getObservers().contains(player)) {
                player.send(this.createText(0));
            }
        }

        @Handler(priority = Priority.LAST)
        public void respawn(PlayerRespawnEvent event) {
            GamePlayer player = event.getGamePlayer();
            if (player.isParticipating()) {
                player.send(this.createText(remaining(player)));
            }
        }

        private String createText(int lives) {
            return Messageable.SUCCESS_COLOR + "You have " + ChatColor.GRAY + ChatColor.BOLD + lives + " " +
                    (lives == 1 ? "life" : "lives") + ChatColor.RESET + Messageable.SUCCESS_COLOR + " remaining.";
        }
    }

    private class SoundListener implements Listener {
        @Handler(priority = Priority.LOWER)
        public void eliminate(PlayerEliminateEvent event) {
            Location location = event.getPlayer().getBukkit().getLocation();
            location.getWorld().playSound(location, sound(), 1F, 1F);
        }
    }
}
