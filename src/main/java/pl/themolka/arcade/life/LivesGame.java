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

package pl.themolka.arcade.life;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchEmptyEvent;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchStartedEvent;
import pl.themolka.arcade.respawn.PlayerRespawnEvent;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.PlayerQuitEvent;
import pl.themolka.arcade.team.Team;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class LivesGame extends GameModule {
    public static final int DEATH_REVOKE = -1;
    public static final int ZERO = 0;

    private Match match;

    private final int lives;
    private final Team fallbackTeam;
    private final boolean announce;
    private final Sound sound;

    private final Map<GamePlayer, Integer> remaining = new WeakHashMap<>();
    private final Set<GamePlayer> eliminated = new LinkedHashSet<>();

    protected LivesGame(Game game, IGameConfig.Library library, Config config) {
        this.lives = config.lives().get();
        this.fallbackTeam = library.getOrDefine(game, config.fallbackTeam().getIfPresent());
        this.announce = config.announce().get();
        this.sound = config.sound().get();
    }

    @Override
    public void onEnable() {
        this.match = ((MatchGame) this.getGame().getModule(MatchModule.class)).getMatch();
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        if (this.announce()) {
            register.add(new AnnounceListener());
        }

        if (this.sound() != null) {
            register.add(new SoundListener());
        }

        return register;
    }

    public int lives() {
        return this.lives;
    }

    public Team fallbackTeam() {
        return this.fallbackTeam != null ? this.fallbackTeam : this.match.getObservers();
    }

    public boolean announce() {
        return this.announce;
    }

    public Sound sound() {
        return this.sound;
    }

    public Set<GamePlayer> eliminated() {
        return new LinkedHashSet<>(this.eliminated);
    }

    public boolean isEliminated(GamePlayer player) {
        return this.eliminated.contains(player);
    }

    //
    // Lives
    //

    public int addLives(GamePlayer player, int lives) {
        int newValue = this.calculate(player, lives);
        this.remaining.put(player, newValue);

        // eliminate the player if the newValue is zero or negative
        if (newValue <= ZERO) {
            this.eliminate(player, this.fallbackTeam());
        }

        return newValue;
    }

    public int calculate(GamePlayer player, int lives) {
        return remaining(player) + lives;
    }

    public int remaining(GamePlayer player) {
        return this.remaining.getOrDefault(player, this.lives());
    }

    public void eliminate(GamePlayer player, Team fallbackTeam) {
        if (this.remaining.containsKey(player)) {
            PlayerEliminateEvent event = new PlayerEliminateEvent(this.getPlugin(),
                    player, this.remaining.getOrDefault(player, ZERO), player.getBukkit().getLocation());
            this.getPlugin().getEventBus().publish(event);

            if (event.isCanceled() && fallbackTeam != null) {
                this.remaining.put(event.getPlayer(), event.getRemainingLives());
                return;
            }

            GamePlayer eventPlayer = event.getPlayer();
            this.remaining.remove(eventPlayer);
            this.eliminated.add(eventPlayer);

            if (fallbackTeam != null) {
                fallbackTeam.join(eventPlayer, true, true);
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
        this.eliminate(event.getGamePlayer(), null);
    }

    @Handler(priority = Priority.LAST)
    public void fillMap(MatchStartedEvent event) {
        for (ArcadePlayer player : event.getPlugin().getPlayers()) {
            this.remaining.put(player.getGamePlayer(), this.lives());
        }
    }

    @Handler(priority = Priority.LOWEST)
    public void matchEmpty(MatchEmptyEvent event) {
        // The match may not end if it's empty.
        event.setCanceled(true);
    }

    //
    // Broadcasts
    //

    private class AnnounceListener {
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
        Sound DEFAULT_SOUND = Sound.ENTITY_IRON_GOLEM_DEATH;

        Ref<Integer> lives();
        default Ref<Team.Config> fallbackTeam() { return Ref.empty(); }
        Ref<Boolean> announce();
        default Ref<Sound> sound() { return Ref.ofProvided(DEFAULT_SOUND); }

        @Override
        default LivesGame create(Game game, Library library) {
            return new LivesGame(game, library, this);
        }
    }
}
