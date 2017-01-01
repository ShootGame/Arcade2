package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.team.Team;

import java.util.List;

public class Observers extends Team {
    public static final ChatColor OBSERVERS_COLOR = ChatColor.DARK_AQUA;
    public static final DyeColor OBSERVERS_DYE_COLOR = DyeColor.CYAN;
    public static final String OBSERVERS_NAME = "Observers";
    public static final int OBSERVERS_SLOTS = Integer.MAX_VALUE;
    public static final String OBSERVERS_TEAM_ID = "_observers-team";

    public Observers(ArcadePlugin plugin, Match match) {
        super(plugin, match, OBSERVERS_TEAM_ID);
    }

    @Override
    public boolean addGoal(Goal goal) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public boolean areGoalsScored() {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public List<Goal> getGoals() {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public int getMaxPlayers() {
        return this.getSlots();
    }

    @Override
    public String getMessage() {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public int getMinPlayers() {
        return 0;
    }

    @Override
    public int getSlots() {
        return OBSERVERS_SLOTS;
    }

    @Override
    public boolean isFriendlyFire() {
        return true;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean isObservers() {
        return true;
    }

    @Override
    public boolean isOverfill() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean isWinning() {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public boolean removeGoal(Goal goal) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public void setFriendlyFire(boolean friendlyFire) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public void setMinPlayers(int minPlayers) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public void setSlots(int slots) {
        throw new UnsupportedOperationException("Not supported here.");
    }
}
