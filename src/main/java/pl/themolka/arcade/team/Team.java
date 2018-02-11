package pl.themolka.arcade.team;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.channel.ChatChannel;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCreateEvent;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchWinner;
import pl.themolka.arcade.scoreboard.ScoreboardContext;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.util.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A group with defined players in it.
 */
public class Team implements MatchWinner {
    public static final int NAME_MAX_LENGTH = 16;

    private static ToStringStyle toStringStyle = ToStringStyle.NO_FIELD_NAMES_STYLE;

    private final ArcadePlugin plugin;

    private final TeamApplyContext applyContext;
    private org.bukkit.scoreboard.Team bukkit;
    private final TeamChannel channel;
    private ChatColor chatColor;
    private DyeColor dyeColor;
    private boolean friendlyFire;
    private final List<Goal> goals = new ArrayList<>();
    private final String id;
    private Match match;
    private int maxPlayers;
    private final Set<GamePlayer> members = new HashSet<>();
    private int minPlayers;
    private String name;
    private final Set<GamePlayer> onlineMembers = new HashSet<>();
    private int slots;

    public Team(ArcadePlugin plugin, String id) {
        this.plugin = plugin;

        this.applyContext = new TeamApplyContext(this);
        this.channel = new TeamChannel(plugin, this);
        this.channel.setFormat(TeamChannel.TEAM_FORMAT);
        this.id = id;
    }

    public Team(Team original) {
        this(original.plugin, original.getId());

        this.setBukkit(original.getBukkit());
        this.setChatColor(original.getChatColor());
        this.setDyeColor(original.getDyeColor());
        this.setFriendlyFire(original.isFriendlyFire());
        this.setMatch(original.getMatch());
        this.setMaxPlayers(original.getMaxPlayers());
        this.setMinPlayers(original.getMinPlayers());
        this.setName(original.getName());
        this.setSlots(original.getSlots());
    }

    @Override
    public boolean addGoal(Goal goal) {
        if (this.hasGoal(goal)) {
            return false;
        }

        GoalCreateEvent.call(this.plugin, goal);
        return this.goals.add(goal);
    }

    @Override
    public boolean contains(Player bukkit) {
        return this.contains(this.plugin.getPlayer(bukkit));
    }

    @Override
    public boolean contains(ArcadePlayer player) {
        return player.getGamePlayer() != null &&
                this.contains(player.getGamePlayer());
    }

    @Override
    public boolean contains(GamePlayer player) {
        return this.hasPlayer(player);
    }

    @Override
    public Color getColor() {
        return Color.ofChat(this.getChatColor());
    }

    @Override
    public List<Goal> getGoals() {
        return new ArrayList<>(this.goals);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<GamePlayer> getPlayers() {
        return new HashSet<>(this.onlineMembers);
    }

    @Override
    public boolean hasGoal(Goal goal) {
        return this.goals.contains(goal);
    }

    @Override
    public boolean removeGoal(Goal goal) {
        return this.goals.remove(goal);
    }

    @Override
    public void sendGoalMessage(String message) {
        this.plugin.getLogger().info("[" + this.getName() + "] (Goal) " +
                ChatColor.stripColor(message));
        this.getChannel().send(ChatColor.YELLOW + message);
        this.getChannel().sendAction(ChatColor.YELLOW + message);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Team && ((Team) obj).getId().equals(this.getId());
    }

    public TeamApplyContext getApplyContext() {
        return this.applyContext;
    }

    public org.bukkit.scoreboard.Team getBukkit() {
        return this.bukkit;
    }

    public TeamChannel getChannel() {
        return this.channel;
    }

    public ChatColor getChatColor() {
        return this.chatColor;
    }

    public DyeColor getDyeColor() {
        return this.dyeColor;
    }

    public Game getGame() {
        return this.getMatch().getGame();
    }

    public Match getMatch() {
        return this.match;
    }

    public int getMaxPlayers() {
        if (this.maxPlayers >= this.slots) {
            return this.maxPlayers;
        }

        return this.slots;
    }

    public Collection<GamePlayer> getMembers() {
        return new ArrayList<>(this.members);
    }

    public int getMinPlayers() {
        return this.minPlayers;
    }

    public Collection<GamePlayer> getOnlineMembers() {
        return new ArrayList<>(this.onlineMembers);
    }

    public String getPrettyName() {
        return this.getTitle();
    }

    public int getSlots() {
        if (this.slots <= this.maxPlayers) {
            return this.slots;
        }

        return this.maxPlayers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public boolean hasPlayed(GamePlayer player) {
        return this.getMembers().contains(player);
    }

    public boolean hasPlayer(GamePlayer player) {
        return this.getOnlineMembers().contains(player);
    }

    public boolean isFriendlyFire() {
        return this.friendlyFire;
    }

    public boolean isFull() {
        return this.getOnlineMembers().size() >= this.getSlots();
    }

    public boolean isObservers() {
        return false;
    }

    public boolean isObserving() {
        return !this.isPlaying();
    }

    public boolean isOverfilled() {
        return this.getOnlineMembers().size() >= this.getMaxPlayers();
    }

    public boolean isParticipating() {
        return !this.isObservers();
    }

    public boolean isPlaying() {
        return this.getMatch().isRunning();
    }

    public boolean join(GamePlayer player) {
        return this.join(player, true);
    }

    public boolean join(GamePlayer player, boolean message) {
        return this.join(player, message, false);
    }

    public boolean join(GamePlayer player, boolean message, boolean force) {
        if (!player.isOnline() || this.isFull() || this.hasPlayer(player)) {
            return false;
        }

        PlayerJoinTeamEvent event = new PlayerJoinTeamEvent(
                this.plugin, player, this);
        this.plugin.getEventBus().publish(event);

        if (!force && event.isCanceled()) {
            return false;
        }

        // handle
        this.members.add(player);
        this.onlineMembers.add(player);
        this.getBukkit().addPlayer(player.getBukkit());

        player.setMetadata(TeamsModule.class, TeamsModule.METADATA_TEAM, this);
        player.setChatColor(this.getChatColor());
        player.setCurrentChannel(this.getCurrentChannel());
        player.setDisplayName(this.getChatColor() +
                player.getUsername() + ChatColor.RESET);
        player.setParticipating(this.getMatch().isRunning() &&
                this.isParticipating());

        // handle it AFTER the setParticipating(...) method
        if (this.getMatch().isRunning()) {
            player.getPlayer().getPermissions().clearGroups();
            player.getPlayer().getPermissions().refresh();
        }

        this.plugin.getLogger().info(player.getUsername() + " joined team '" +
                this.getName() + "' (" + this.getId() + ")");
        if (message) {
            player.getPlayer().sendSuccess("You joined " +
                    this.getPrettyName() + ChatColor.GREEN + ".");
        }

        this.plugin.getEventBus().publish(new PlayerJoinedTeamEvent(
                this.plugin, player, this));
        return true;
    }

    public void joinForce(GamePlayer player) {
        this.join(player, true, true);
    }

    public boolean leave(GamePlayer player) {
        return this.leave(player, false);
    }

    public boolean leave(GamePlayer player, boolean force) {
        if (!player.isOnline() || !this.hasPlayer(player)) {
            return false;
        }

        PlayerLeaveTeamEvent event = new PlayerLeaveTeamEvent(
                this.plugin, player, this);
        this.plugin.getEventBus().publish(event);

        if (!force && event.isCanceled()) {
            return false;
        }

        // handle
        this.members.remove(player);
        this.onlineMembers.remove(player);
        this.getBukkit().removePlayer(player.getBukkit());

        player.removeMetadata(TeamsModule.class, TeamsModule.METADATA_TEAM);
        player.setChatColor(null);
        player.setCurrentChannel(null);
        player.setParticipating(false);
        player.resetDisplayName();

        // handle it AFTER the setParticipating method
        if (this.getMatch().isRunning()) {
            player.getPlayer().getPermissions().clearGroups();
            player.getPlayer().getPermissions().refresh();
        }

        this.plugin.getLogger().info(player.getUsername() + " left team '" +
                this.getName() + "' (" + this.getId() + ")");

        this.plugin.getEventBus().publish(new PlayerLeftTeamEvent(
                this.plugin, player, this));
        return true;
    }

    public void leaveForce(GamePlayer player) {
        this.leave(player, true);
    }

    public void leaveServer(GamePlayer player) {
        this.onlineMembers.remove(player);

        if (player.getBukkit() != null) {
            this.getBukkit().removePlayer(player.getBukkit());
        }

        this.plugin.getLogger().info(player.getUsername() + " left team '" +
                this.getName() + "' (" + this.getId() + ") <server quit>");
    }

    public void send(String message) {
        for (GamePlayer player : this.getOnlineMembers()) {
            player.getPlayer().send(message);
        }
    }

    public void setBukkit(org.bukkit.scoreboard.Team bukkit) {
        this.bukkit = bukkit;

        this.updateBukkitTeam();
    }

    public void setChatColor(ChatColor chatColor) {
        this.chatColor = chatColor;
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
        if (name.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Name too long (" +
                    name.length() + " > " + NAME_MAX_LENGTH + ")");
        }

        this.name = name;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, toStringStyle)
                .append("id", this.getId())
                .build();
    }

    private ChatChannel getCurrentChannel() {
        if (this.getMatch().isRunning()) {
            return this.getChannel();
        } else {
            return null;
        }
    }

    private void updateBukkitTeam() {
        if (this.getBukkit() != null) {
            this.getBukkit().setAllowFriendlyFire(this.isFriendlyFire());
            this.getBukkit().setCanSeeFriendlyInvisibles(true);
            this.getBukkit().setOption(
                    org.bukkit.scoreboard.Team.Option.COLLISION_RULE,
                    org.bukkit.scoreboard.Team.OptionStatus.NEVER);
        }
    }

    public static org.bukkit.scoreboard.Team createBukkitTeam(
            Scoreboard scoreboard, Team team) {
        String id = team.getId();
        if (id.length() > ScoreboardContext.TEAM_MAX_LENGTH) {
            id = id.substring(0, ScoreboardContext.TEAM_MAX_LENGTH);
        }

        org.bukkit.scoreboard.Team bukkit = scoreboard.registerNewTeam(id);
        bukkit.setPrefix(team.getChatColor().toString());
        bukkit.setDisplayName(team.getName());
        bukkit.setSuffix(ChatColor.RESET.toString());

        return bukkit;
    }
}
