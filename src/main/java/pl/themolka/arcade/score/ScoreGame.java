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

package pl.themolka.arcade.score;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.life.PlayerDeathEvent;
import pl.themolka.arcade.match.DynamicWinnable;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchWinner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScoreGame extends GameModule implements DynamicWinnable {
    private final Map<Participator, Score> byOwner = new HashMap<>();
    private final Set<ScoreBox> scoreBoxes = new LinkedHashSet<>();

    private Match match;

    protected ScoreGame(Game game, IGameConfig.Library library, Config config) {
        for (Score.Config score : config.scores().get()) {
            this.byOwner.put(library.getOrDefine(game, score.owner().get()),
                             library.getOrDefine(game, score));
        }

        for (ScoreBox.Config scoreBox : config.scoreBoxes().getOrDefault(Collections.emptySet())) {
            this.scoreBoxes.add((ScoreBox) library.getOrDefine(game, scoreBox));
        }
    }

    @Override
    public void onEnable() {
        MatchGame module = (MatchGame) this.getGame().getModule(MatchModule.class);
        this.match = module.getMatch();
        this.match.registerDynamicWinnable(this);

        for (Map.Entry<Participator, Score> entry : this.byOwner.entrySet()) {
            MatchWinner winner = this.match.findWinnerById(entry.getKey().getId());
            if (winner == null) {
                continue;
            }

            Score score = entry.getValue();
            score.setMatch(this.match);

            winner.addGoal(score);
        }
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        if (!this.scoreBoxes.isEmpty()) {
            register.add(new ScoreBoxListeners(this));
        }
        return super.onListenersRegister(register);
    }

    @Override
    public List<MatchWinner> getDynamicWinners() {
        double highestScore = Score.MIN;

        List<MatchWinner> results = new ArrayList<>();
        for (MatchWinner winner : this.getMatch().getWinnerList()) {
            if (winner.areGoalsCompleted()) {
                results.add(winner);
            }

            Score score = this.getScore(winner);
            if (score == null) {
                continue;
            } else if (score.getScore() > highestScore) {
                results.clear();
                highestScore = score.getScore();
            }

            if (!results.contains(winner) && score.getScore() >= highestScore) {
                results.add(winner);
            }
        }

        if (highestScore != Score.MIN && !results.isEmpty()) {
            return results;
        }

        return null;
    }

    public Collection<Score> getAll() {
        return this.byOwner.values();
    }

    public Match getMatch() {
        return this.match;
    }

    public Score getScore(Participator holder) {
        return this.byOwner.get(holder);
    }

    public ScoreBox getScoreBox(Vector at) {
        for (ScoreBox scoreBox : this.scoreBoxes) {
            if (scoreBox.getFieldStrategy().regionContains(scoreBox, at)) {
                return scoreBox;
            }
        }

        return null;
    }

    public boolean hasAnyScoreBoxes() {
        return !this.scoreBoxes.isEmpty();
    }

    @Handler(priority = Priority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Match match = this.getMatch();

        if (match != null) {
            // kill registration
            boolean wasKill = this.registerPoints(match, event.getKiller(), true);

            // death registration
            if (!wasKill) {
                this.registerPoints(match, event.getVictim(), false);
            }
        }
    }

    @Handler(priority = Priority.FIRST)
    public void onScoreBoxReach(ScoreBoxEvent event) {
        // Round the points, if the result is zero (it
        // is a long), then return a real double value.

        double points = Math.floor(event.getPoints());
        if (points == 0D) {
            points = event.getPoints();
        }

        this.getMatch().sendGoalMessage(ChatColor.GOLD + event.getPlayer().getDisplayName() +
                ChatColor.YELLOW + " scored " + ChatColor.GOLD + ChatColor.BOLD + ChatColor.ITALIC +
                points + " points" + ChatColor.RESET + ChatColor.YELLOW + " for " + ChatColor.GOLD +
                event.getScore().getOwner().getTitle() + ChatColor.YELLOW + ".");
    }

    @Handler(priority = Priority.FIRST)
    public void onScoreLimitReach(ScoreLimitReachEvent event) {
        this.getMatch().sendGoalMessage(ChatColor.YELLOW + "The score limit of " + ChatColor.GOLD + ChatColor.BOLD +
                Math.round(event.getLimit()) + " points" + ChatColor.RESET + ChatColor.YELLOW + " for " + ChatColor.GOLD +
                event.getScore().getOwner().getTitle() + ChatColor.YELLOW + " has been reached.");
    }

    private boolean registerPoints(Match match, GamePlayer player, boolean killReward) {
        if (player == null) {
            return false;
        }

        Participator competitor = match.findWinnerByPlayer(player);
        if (competitor != null) {
            Score score = this.getScore(competitor);

            if (score != null) {
                double value = killReward ? score.getKillReward() : -score.getDeathLoss();

                if (value != Score.ZERO) {
                    score.incrementScore(player, value);
                }
                return true;
            }
        }

        return false;
    }

    public interface Config extends IGameModuleConfig<ScoreGame> {
        Ref<Set<Score.Config>> scores();
        default Ref<Set<ScoreBox.Config>> scoreBoxes() { return Ref.empty(); }

        @Override
        default ScoreGame create(Game game, Library library) {
            return new ScoreGame(game, library, this);
        }
    }
}
