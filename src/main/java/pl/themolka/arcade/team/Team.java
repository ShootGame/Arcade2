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
    private boolean friendlyFire;
    private final String id;
    private Match match;
    private int maxPlayers;
    private final List<GamePlayer> members = new ArrayList<>();
    private int minPlayers;
    private String name;
    private final List<GamePlayer> onlineMembers = new ArrayList<>();
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

    public boolean isFriendlyFire() {
        return this.friendlyFire;
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

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public List<GamePlayer> getMembers() {
        return this.members;
    }

    public int getMinPlayers() {
        return this.minPlayers;
    }

    public String getName() {
        return this.name;
    }

    public List<GamePlayer> getOnlineMembers() {
        return this.onlineMembers;
    }

    public String getPrettyName() {
        return this.getColor() + this.getName() + ChatColor.RESET;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public boolean hasPlayer(GamePlayer player) {
        return this.getMembers().contains(player);
    }

    public boolean isFull() {
        return this.getOnlineMembers().size() >= this.getMaxPlayers();
    }

    public boolean isObservers() {
        return false;
    }

    public boolean isObserving() {
        return !this.isPlaying();
    }

    public boolean isOverfill() {
        return this.getOnlineMembers().size() >= this.getSlots();
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
        if (!player.isOnline() || this.isFull()) {
            return;
        }

        PlayerJoinTeamEvent event = new PlayerJoinTeamEvent(this.plugin, player, this);
        this.plugin.getEventBus().publish(event);

        if (!event.isCanceled()) {
            this.members.add(player);
            this.onlineMembers.add(player);

            player.setMetadata(TeamsModule.class, TeamsModule.METADATA_TEAM, this);
            player.getPlayer().getBukkit().setDisplayName(this.getColor() + player.getUsername());

            if (message) {
                player.getPlayer().sendSuccess("You joined the " + this.getPrettyName() + ChatColor.GREEN + ".");
            }

            this.plugin.getEventBus().publish(new PlayerJoinedTeamEvent(this.plugin, player, this));
        }
    }

    public void leave(GamePlayer player) {
        if (!player.isOnline()) {
            return;
        }

        PlayerLeaveTeamEvent event = new PlayerLeaveTeamEvent(this.plugin, player, this);
        this.plugin.getEventBus().publish(event);

        if (!event.isCanceled()) {
            this.members.remove(player);
            this.onlineMembers.remove(player);

            player.removeMetadata(TeamsModule.class, TeamsModule.METADATA_TEAM);

            this.plugin.getEventBus().publish(new PlayerLeftTeamEvent(this.plugin, player, this));
        }
    }

    public void leaveServer(GamePlayer player) {
        this.onlineMembers.remove(player);
    }

    public void send(String message) {
        for (GamePlayer player : this.getOnlineMembers()) {
            player.getPlayer().send(message);
        }
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public void setDyeColor(DyeColor dyeColor) {
        this.dyeColor = dyeColor;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }
}
