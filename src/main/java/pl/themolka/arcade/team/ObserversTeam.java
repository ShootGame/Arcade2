package pl.themolka.arcade.team;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.match.Match;

public class ObserversTeam extends Team {
    public static final ChatColor OBSERVERS_COLOR = ChatColor.DARK_AQUA;
    public static final DyeColor OBSERVERS_DYE_COLOR = DyeColor.CYAN;
    public static final String OBSERVERS_NAME = "Observers";
    public static final int OBSERVERS_SLOTS = Integer.MAX_VALUE;
    public static final String OBSERVERS_TEAM_ID = "_observers-team";

    public ObserversTeam(ArcadePlugin plugin, Match match) {
        super(plugin, match, OBSERVERS_TEAM_ID);

        this.setColor(OBSERVERS_COLOR);
        this.setDyeColor(OBSERVERS_DYE_COLOR);
        this.setName(OBSERVERS_NAME);
    }

    @Override
    public boolean isObservers() {
        return true;
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
    public void setSlots(int slots) {
        throw new UnsupportedOperationException("Not supported here.");
    }
}
