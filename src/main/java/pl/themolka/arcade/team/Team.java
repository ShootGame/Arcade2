package pl.themolka.arcade.team;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {
    private final ArcadePlugin plugin;

    private ChatColor color;
    private DyeColor dyeColor;
    private final String id;
    private final Match match;
    private final List<GamePlayer> members = new ArrayList<>();
    private String name;
    private int slots;

    public Team(ArcadePlugin plugin, Match match, String id) {
        this.plugin = plugin;

        this.id = id;
        this.match = match;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Team && ((Team) obj).getId().equals(this.getId());
    }

    public ChatColor getColor() {
        return this.color;
    }

    public DyeColor getDyeColor() {
        return this.dyeColor;
    }

    public Game getGame() {
        return this.getMatch().getGame();
    }

    public String getId() {
        return this.id;
    }

    public Match getMatch() {
        return this.match;
    }

    public List<GamePlayer> getMembers() {
        return this.members;
    }

    public String getName() {
        return this.name;
    }

    public String getPrettyName() {
        return this.getColor() + this.getName() + ChatColor.RESET;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public boolean isObservers() {
        return false;
    }

    public boolean isObserving() {
        return !this.isPlaying();
    }

    public boolean isPlaying() {
        return this.getMatch().getState().equals(MatchState.RUNNING);
    }

    public int getSlots() {
        return this.slots;
    }

    public void join(GamePlayer player) {
        this.join(player, true);
    }

    public void join(GamePlayer player, boolean message) {
        if (!player.isOnline()) {
            return;
        }

        PlayerJoinTeamEvent event = new PlayerJoinTeamEvent(this.plugin, player, this);
        this.plugin.getEvents().post(event);

        if (!event.isCanceled()) {
            this.members.add(player);
            player.setMetadata(TeamsModule.class, TeamsModule.METADATA_TEAM, this);

            if (message) {
                player.getPlayer().sendSuccess("You joined the " + this.getPrettyName() + ChatColor.GREEN + ".");
            }
        }
    }

    public void leave(GamePlayer player) {
        if (!player.isOnline()) {
            return;
        }

        PlayerLeaveTeamEvent event = new PlayerLeaveTeamEvent(this.plugin, player, this);
        this.plugin.getEvents().post(event);

        if (!event.isCanceled()) {
            this.members.remove(player);
            player.removeMetadata(TeamsModule.class, TeamsModule.METADATA_TEAM);
        }
    }

    public void send(String message) {
        for (GamePlayer player : this.getMembers()) {
            if (player.isOnline()) {
                player.getPlayer().send(message);
            }
        }
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public void setDyeColor(DyeColor dyeColor) {
        this.dyeColor = dyeColor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }
}
