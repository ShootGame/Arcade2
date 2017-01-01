package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.team.Team;

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
    public int getMaxPlayers() {
        return this.getSlots();
    }

    @Override
    public int getMinPlayers() {
        return 0;
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
    public int getSlots() {
        return OBSERVERS_SLOTS;
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
