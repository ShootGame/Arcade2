package pl.themolka.arcade.life;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.match.DynamicWinnable;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchEmptyEvent;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchWinner;
import pl.themolka.arcade.team.PlayerJoinedTeamEvent;
import pl.themolka.arcade.team.PlayerLeftTeamEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KillEnemiesGame extends GameModule implements DynamicWinnable {
    private final Map<Participator, KillEnemies> byOwner = new HashMap<>();

    private Match match;

    protected KillEnemiesGame(Game game, IGameConfig.Library library, Config config) {
        for (KillEnemies.Config objective : config.objectives()) {
            this.byOwner.put(library.getOrDefine(game, objective.owner().get()),
                             library.getOrDefine(game, objective));
        }
    }

    @Override
    public void onEnable() {
        MatchGame module = (MatchGame) this.getGame().getModule(MatchModule.class);
        this.match = module.getMatch();
        this.match.registerDynamicWinnable(this);

        for (Map.Entry<Participator, KillEnemies> entry : this.byOwner.entrySet()) {
            MatchWinner winner = this.match.findWinnerById(entry.getKey().getId());
            if (winner == null) {
                continue;
            }

            KillEnemies goal = entry.getValue();
            goal.injectParticipatorResolver(this.match);

            winner.addGoal(goal);
        }
    }

    @Override
    public List<MatchWinner> getDynamicWinners() {
        double highestProgress = Goal.PROGRESS_UNTOUCHED.getValue();

        List<MatchWinner> results = new ArrayList<>();
        for (MatchWinner winner : this.getMatch().getWinnerList()) {
            if (winner.areGoalsCompleted()) {
                results.add(winner);
                continue;
            }

            KillEnemies objective = this.getObjective(winner);
            if (objective == null) {
                continue;
            }

            double progress = objective.getProgress().getValue();
            if (progress > highestProgress) {
                results.clear();
                highestProgress = progress;
            }

            if (!results.contains(winner) && progress >= highestProgress) {
                results.add(winner);
            }
        }

        if (highestProgress != Goal.PROGRESS_UNTOUCHED.getValue() && !results.isEmpty()) {
            return results;
        }

        return null;
    }

    public Match getMatch() {
        return this.match;
    }

    public KillEnemies getObjective(Participator holder) {
        return this.byOwner.get(holder);
    }

    public Collection<KillEnemies> getObjectives() {
        return this.byOwner.values();
    }

    private Set<Participator> fetchEnemies(Collection<MatchWinner> all, MatchWinner owner) {
        Set<Participator> enemies = new HashSet<>();
        for (Participator competitor : all) {
            if (!competitor.equals(owner)) {
                enemies.add(competitor);
            }
        }

        return enemies;
    }

    //
    // Refreshing Objectives
    //

    public void refreshObjectives(Participator completer) {
        for (KillEnemies objective : this.byOwner.values()) {
            if (objective.isCompleted()) {
                objective.setCompleted(true, completer);
            }
        }
    }

    @Handler(priority = Priority.LOWEST)
    public void matchEmpty(MatchEmptyEvent event) {
        // The match may not end if it's empty.
        event.setCanceled(true);
    }

    @Handler(priority = Priority.LOWEST)
    public void teamJoined(PlayerJoinedTeamEvent event) {
        this.refreshObjectives(event.getGamePlayer());
    }

    @Handler(priority = Priority.LOWEST)
    public void teamLeft(PlayerLeftTeamEvent event) {
        this.refreshObjectives(event.getGamePlayer());
    }

    public interface Config extends IGameModuleConfig<KillEnemiesGame> {
        Set<KillEnemies.Config> objectives();

        @Override
        default KillEnemiesGame create(Game game, Library library) {
            return new KillEnemiesGame(game, library, this);
        }
    }
}
