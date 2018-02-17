package pl.themolka.arcade.game;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.StringId;

import java.util.Collections;
import java.util.Set;

public interface Participator extends GoalHolder, Participartable, StringId {
    ToStringStyle TO_STRING_STYLE = ToStringStyle.NO_FIELD_NAMES_STYLE;

    boolean contains(Player bukkit);

    default boolean contains(ArcadePlayer player) {
        GamePlayer gamePlayer = player.getGamePlayer(); // should never be null
        return gamePlayer != null && this.contains(gamePlayer);
    }

    default boolean contains(GamePlayer player) {
        Player bukkit = player.getBukkit(); // null if the player if offline
        return bukkit != null && this.contains(bukkit);
    }

    Color getColor();

    default String getName() {
        return this.getId();
    }

    default Set<GamePlayer> getPlayers() {
        return Collections.emptySet();
    }

    default String getTitle() {
        return this.getColor().toChat() + this.getName() + ChatColor.RESET;
    }

    void sendGoalMessage(String message);
}
