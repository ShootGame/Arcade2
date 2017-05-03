package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.team.Team;
import pl.themolka.arcade.util.Color;

import java.util.List;

/**
 * A non-participating team observing the match and its players.
 */
public class Observers extends Team {
    public static final ChatColor OBSERVERS_CHAT_COLOR = ChatColor.AQUA;
    public static final DyeColor OBSERVERS_DYE_COLOR =
            Color.ofChat(OBSERVERS_CHAT_COLOR).toDye();
    public static final String OBSERVERS_KEY = "@";
    public static final String OBSERVERS_NAME = "Observers";
    public static final int OBSERVERS_SLOTS = Integer.MAX_VALUE;
    public static final String OBSERVERS_TEAM_ID = "_observers-team";

    private final ArcadePlugin plugin;

    public Observers(ArcadePlugin plugin) {
        super(plugin, OBSERVERS_TEAM_ID);
        this.plugin = plugin;
    }

    @Override
    public boolean addGoal(Goal goal) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public boolean areGoalsCompleted() {
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
    public boolean hasGoal(Goal goal) {
        return false;
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
    public boolean isOverfilled() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean join(GamePlayer player, boolean message) {
        boolean result = super.join(player, message);
        if (result) {
            player.getPlayer().clearInventory(true);
            player.setParticipating(false);
            player.getBukkit().setAllowFlight(true);

            this.plugin.getEventBus().publish(
                    new ObserversJoinEvent(this.plugin, player, this));
        }

        return result;
    }

    @Override
    public boolean leave(GamePlayer player) {
        boolean result = super.leave(player);
        if (result) {
            this.plugin.getEventBus().publish(
                    new ObserversLeaveEvent(this.plugin, player, this));
        }

        return result;
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
