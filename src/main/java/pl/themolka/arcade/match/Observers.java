package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.team.Team;
import pl.themolka.arcade.util.Color;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A non-participating team observing the match and its players.
 */
public class Observers extends Team {
    public static final ChatColor OBSERVERS_CHAT_COLOR = ChatColor.AQUA;
    public static final DyeColor OBSERVERS_DYE_COLOR = Color.ofChat(OBSERVERS_CHAT_COLOR).toDye();
    public static final String OBSERVERS_KEY = "@";
    public static final String OBSERVERS_NAME = "Observers";
    public static final int OBSERVERS_SLOTS = Integer.MAX_VALUE;
    public static final String OBSERVERS_TEAM_ID = "_observers-team";

    protected Observers(Game game, Config config) {
        super(game, config);
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
    public boolean canParticipate() {
        return false;
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
    public Set<GamePlayer> getPlayers() {
        Set<GamePlayer> players = new HashSet<>();
        for (GamePlayer player : this.getGame().getPlayers().getOnlinePlayers()) {
            if (player.isParticipating()) {
                players.add(player);
            }
        }

        return players;
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
    public boolean isParticipating() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean join(GamePlayer player, boolean message, boolean force) {
        boolean result = super.join(player, message, force);
        if (result) {
            player.getPlayer().clearInventory(true);
            player.setParticipating(false);
            player.getBukkit().setAllowFlight(true);

            this.getPlugin().getEventBus().publish(new ObserversJoinEvent(this.getPlugin(), player, this));
        }

        return result;
    }

    @Override
    public boolean leave(GamePlayer player) {
        boolean result = super.leave(player);
        if (result) {
            this.getPlugin().getEventBus().publish(new ObserversLeaveEvent(this.getPlugin(), player, this));
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
    public void setParticipating(boolean participating) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    @Override
    public void setSlots(int slots) {
        throw new UnsupportedOperationException("Not supported here.");
    }

    public interface Config extends Team.Config {
        default String id() { return OBSERVERS_TEAM_ID; }
        default ChatColor chatColor() { return OBSERVERS_CHAT_COLOR; }
        default DyeColor dyeColor() { return OBSERVERS_DYE_COLOR; }
        default boolean friendlyFire() { return true; }
        default int minPlayers() { return 0; }
        default int maxPlayers() { return OBSERVERS_SLOTS; }
        default String name() { return OBSERVERS_NAME; }
        default int slots() { return OBSERVERS_SLOTS; }

        @Override
        default Team create(Game game, Library library) {
            return new Observers(game, this);
        }
    }
}
