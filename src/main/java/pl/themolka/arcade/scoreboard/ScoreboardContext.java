package pl.themolka.arcade.scoreboard;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;

public class ScoreboardContext {
    public static final String OBJECTIVE_NAME = StringUtils.substring(ArcadePlugin.class.getSimpleName(), 0, 16);
    public static final String OBJECTIVE_CRITERION = "dummy";
    public static final String OBJECTIVE_DEFAULT_NAME = ChatColor.RED + "Scoreboard"; // 12 chars
    public static final int OBJECTIVE_MAX_LENGTH = 32;

    public static final int TEAM_MAX_LENGTH = 16;

    private final ArcadePlugin plugin;
    private final Game game;

    private final Scoreboard scoreboard;
    private final Objective objective;

    public ScoreboardContext(ArcadePlugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;

        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();

        this.objective = this.scoreboard.registerNewObjective(OBJECTIVE_NAME, OBJECTIVE_CRITERION);
        this.objective.setDisplayName(this.getScoreboardTitle(game));
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.objective.getScore("Score").setScore(10);
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public Objective getObjective() {
        return this.objective;
    }

    private String getScoreboardTitle(Game game) {
        String name = game.getMap().getScoreboardTitle();
        if (name != null) {
            return name;
        }

        return OBJECTIVE_DEFAULT_NAME;
    }

    public void unregister() {
        // unregister objectives
        for (Objective objective : this.getScoreboard().getObjectives()) {
            objective.unregister();
        }

        // unregister teams
        for (Team team : this.getScoreboard().getTeams()) {
            team.unregister();
        }
    }
}
