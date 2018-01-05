package pl.themolka.arcade.game;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.channel.ChatChannel;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCreateEvent;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.kit.FlyContent;
import pl.themolka.arcade.kit.FoodLevelContent;
import pl.themolka.arcade.kit.HealthContent;
import pl.themolka.arcade.kit.KnockbackContent;
import pl.themolka.arcade.kit.SaturationContent;
import pl.themolka.arcade.kit.WalkSpeedContent;
import pl.themolka.arcade.metadata.Metadata;
import pl.themolka.arcade.metadata.MetadataContainer;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.util.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * GamePlayers are secure to be stored when the players are offline. The
 * GamePlayers are created on every game cycle and when players join the server
 * (and they didn't played this game yet, since GamePlayer is restored on every
 * rejoin). The GamePlayers are removed only on game cycles. GamePlayer is
 * secure to store game related data.
 */
public class GamePlayer implements GoalHolder, Metadata, Sender {
    public static final ChatColor DEFAULT_CHAT_COLOR = ChatColor.YELLOW;

    private ChatChannel channel;
    private ChatColor chatColor;
    private String displayName;
    private final Game game;
    private final List<Goal> goals = new ArrayList<>();
    private final MetadataContainer metadata = new MetadataContainer();
    private boolean participating;
    // Pointer to the ArcadePlayer instance, or null if the players is offline.
    private ArcadePlayer player;
    private final String username;
    private final UUID uuid;

    public GamePlayer(Game game, ArcadePlayer player) {
        this(game, player.getUsername(), player.getUuid());

        this.setPlayer(player);
    }

    public GamePlayer(Game game, String username, UUID uuid) {
        this.game = game;
        this.username = username;
        this.uuid = uuid;
    }

    @Override
    public boolean addGoal(Goal goal) {
        if (this.hasGoal(goal)) {
            return false;
        }

        GoalCreateEvent.call(this.game.getPlugin(), goal);
        return this.goals.add(goal);
    }

    @Override
    public boolean contains(Player bukkit) {
        Player source = this.getBukkit();
        return bukkit != null && source != null && source.equals(bukkit);
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
        return this.getUuid().toString();
    }

    @Override
    public Locale getLocale() {
        if (this.isOnline()) {
            return this.getPlayer().getLocale();
        } else {
            return DEFAULT_LOCALE;
        }
    }

    @Override
    public Object getMetadata(Class<? extends Module<?>> owner,
                              String key, Object def) {
        return this.metadata.getMetadata(owner, key, def);
    }

    @Override
    public Set<String> getMetadataKeys() {
        return this.metadata.getMetadataKeys();
    }

    @Override
    public ArcadePlayer getPlayer() {
        return this.player;
    }

    @Override
    public String getTitle() {
        return this.getFullName();
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public boolean hasGoal(Goal goal) {
        return this.goals.contains(goal);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.isOnline() && this.getPlayer().hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public boolean removeGoal(Goal goal) {
        return this.goals.remove(goal);
    }

    @Override
    public void send(String message) {
        if (this.isOnline()) {
            this.getPlayer().send(message);
        }
    }

    @Override
    public void sendAction(String action) {
        if (this.isOnline()) {
            this.getPlayer().sendAction(action);
        }
    }

    @Override
    public void sendChat(String chat) {
        if (this.isOnline()) {
            this.getPlayer().sendChat(chat);
        }
    }

    @Override
    public void sendGoalMessage(String message) {
        this.sendAction(message);
    }

    @Override
    public void setMetadata(Class<? extends Module<?>> owner,
                            String key, Object metadata) {
        this.metadata.setMetadata(owner, key, metadata);
    }

    public boolean canSee(GamePlayer player) {
        return this.isOnline() && player.isOnline() &&
                this.getGame() != null &&
                this.getGame().canSee(this, player);
    }

    public Player getBukkit() {
        if (this.isOnline()) {
            return this.getPlayer().getBukkit();
        } else {
            return null;
        }
    }

    public ChatColor getChatColor() {
        if (this.hasChatColor()) {
            return this.chatColor;
        }

        return DEFAULT_CHAT_COLOR;
    }

    public ChatChannel getCurrentChannel() {
        if (this.isOnline()) {
            return this.channel;
        } else {
            return null;
        }
    }

    public String getDisplayName() {
        if (this.displayName != null) {
            return this.displayName;
        } else if (this.isOnline()) {
            return this.getPlayer().getDisplayName();
        } else {
            return this.getUsername();
        }
    }

    public String getFullName() {
        if (this.isOnline()) {
            return this.getPlayer().getFullName();
        } else {
            return this.getDisplayName();
        }
    }

    public Game getGame() {
        return this.game;
    }

    public boolean hasChatColor() {
        return this.chatColor != null;
    }

    public boolean hasDisplayName() {
        return this.isOnline() && (this.displayName != null ||
                this.getPlayer().getDisplayName() != null);
    }

    public boolean isOnline() {
        return this.player != null;
    }

    public boolean isParticipating() {
        return this.isOnline() && this.participating;
    }

    public void refresh() {
        if (!this.isOnline()) {
            return;
        }

        boolean participating = this.isParticipating();
        if (!participating) {
            this.getBukkit().leaveVehicle();
        }

        this.getBukkit().setAffectsSpawning(participating);
        this.getBukkit().setCanPickupItems(participating);
        this.getBukkit().setCollidesWithEntities(participating);
        this.getBukkit().showInvisibles(!participating);
    }

    public void refreshVisibility(Iterable<ArcadePlayer> viewers) {
        if (!this.isOnline()) {
            return;
        }

        for (ArcadePlayer online : viewers) {
            GamePlayer player = online.getGamePlayer();
            if (player == null) {
                continue;
            }

            // can this player see looped player?
            if (this.canSee(player)) {
                this.getBukkit().showPlayer(player.getBukkit());
            } else {
                this.getBukkit().hidePlayer(player.getBukkit());
            }

            // can looped player see this player?
            if (player.canSee(this)) {
                player.getBukkit().showPlayer(this.getBukkit());
            } else {
                player.getBukkit().hidePlayer(this.getBukkit());
            }
        }
    }

    public void reset() {
        if (!this.isOnline()) {
            return;
        }

        Player bukkit = this.getBukkit();
        this.refresh();

        this.getPlayer().clearInventory(true);
        this.resetDisplayName();

        bukkit.setAbsorption(0F);
        bukkit.setAllowFlight(FlyContent.DEFAULT_ALLOW_FLYING);
        bukkit.setArrowsStuck(0);
        bukkit.setExhaustion(0F);
        bukkit.setExp(0);
        bukkit.setFallDistance(0F);
        bukkit.setFireTicks(0);
        bukkit.setFlying(FlyContent.DEFAULT_FLYING);
        bukkit.setFlySpeed(FlyContent.DEFAULT_SPEED);
        bukkit.setFoodLevel(FoodLevelContent.DEFAULT_LEVEL);
        bukkit.setGlowing(false);
        bukkit.setHealthScale(HealthContent.DEFAULT_HEALTH);
        bukkit.setHealth(HealthContent.DEFAULT_HEALTH);
        bukkit.setKnockbackReduction(KnockbackContent.DEFAULT_KNOCKBACK);
        bukkit.setLevel(0);
        bukkit.setSaturation(SaturationContent.DEFAULT_SATURATION);
        bukkit.setSneaking(false);
        bukkit.setSprinting(false);
        bukkit.setWalkSpeed(WalkSpeedContent.DEFAULT_SPEED);

        bukkit.setFastNaturalRegeneration(false);
        bukkit.setSlowNaturalRegeneration(true);

        bukkit.resetPlayerTime();
        bukkit.resetPlayerWeather();
        bukkit.resetTitle();

        for (PotionEffectType potion : PotionEffectType.values()) {
            this.getBukkit().removePotionEffect(potion);
        }
    }

    public void resetDisplayName() {
        this.setDisplayName(null);
    }

    public void setChatColor(ChatColor chatColor) {
        this.chatColor = chatColor;
    }

    public void setCurrentChannel(ChatChannel channel) {
        if (this.isOnline()) {
            this.channel = channel;
        }
    }

    public void setDisplayName(String displayName) {
        if (this.isOnline()) {
            this.displayName = displayName;

            this.getPlayer().setDisplayName(displayName);
        }
    }

    public void setParticipating(boolean participating) {
        if (this.isOnline()) {
            this.participating = participating;
        }
    }

    public void setPlayer(ArcadePlayer player) {
        this.player = player;
    }
}
