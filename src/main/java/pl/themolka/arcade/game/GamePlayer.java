package pl.themolka.arcade.game;

import net.minecraft.server.AttributeMapBase;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.attribute.Attributable;
import pl.themolka.arcade.attribute.Attribute;
import pl.themolka.arcade.attribute.AttributeKey;
import pl.themolka.arcade.attribute.TrackingAttributeMap;
import pl.themolka.arcade.bossbar.BossBarFacet;
import pl.themolka.arcade.channel.ChatChannel;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCreateEvent;
import pl.themolka.arcade.kit.content.AbsorptionContent;
import pl.themolka.arcade.kit.content.BurnContent;
import pl.themolka.arcade.kit.content.CanFlyContent;
import pl.themolka.arcade.kit.content.ExhaustionContent;
import pl.themolka.arcade.kit.content.ExperienceContent;
import pl.themolka.arcade.kit.content.FallDistanceContent;
import pl.themolka.arcade.kit.content.FlyContent;
import pl.themolka.arcade.kit.content.FlySpeedContent;
import pl.themolka.arcade.kit.content.GameModeContent;
import pl.themolka.arcade.kit.content.GlowContent;
import pl.themolka.arcade.kit.content.GravityContent;
import pl.themolka.arcade.kit.content.HealthContent;
import pl.themolka.arcade.kit.content.HealthScaleContent;
import pl.themolka.arcade.kit.content.HungerContent;
import pl.themolka.arcade.kit.content.KnockbackContent;
import pl.themolka.arcade.kit.content.MaxHealthContent;
import pl.themolka.arcade.kit.content.RemoveArrowsContent;
import pl.themolka.arcade.kit.content.SaturationContent;
import pl.themolka.arcade.kit.content.SilentContent;
import pl.themolka.arcade.kit.content.VelocityContent;
import pl.themolka.arcade.kit.content.WalkSpeedContent;
import pl.themolka.arcade.match.MatchWinner;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.PlayerLevel;
import pl.themolka.arcade.time.TimeUtils;
import pl.themolka.arcade.util.Color;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * GamePlayers are secure to be stored when the players are offline. The
 * GamePlayers are created on every game cycle and when players join the server
 * (and they didn't played this game yet, since GamePlayer is restored on every
 * rejoin). The GamePlayers are removed only on game cycles. GamePlayer is
 * secure to store game related data.
 */
public class GamePlayer implements Attributable, GameHolder, MatchWinner, Sender {
    public static final ChatColor DEFAULT_CHAT_COLOR = ChatColor.YELLOW;

    private TrackingAttributeMap attributeMap;
    private final BossBarFacet bossBarFacet;
    private ChatChannel channel;
    private ChatColor chatColor;
    private String displayName;
    private final Game game;
    private final List<Goal> goals = new ArrayList<>();
    private boolean participating;
    // Pointer to the ArcadePlayer instance, or null if the player is offline.
    private Reference<ArcadePlayer> player;
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

        this.bossBarFacet = new BossBarFacet(this);
    }

    @Override
    public boolean addGoal(Goal goal) {
        if (this.hasGoal(goal)) {
            return false;
        }

        GoalCreateEvent.call(goal);
        return this.goals.add(goal);
    }

    @Override
    public boolean canParticipate() {
        return this.isOnline();
    }

    @Override
    public boolean contains(Player bukkit) {
        Player source = this.getBukkit();
        return bukkit != null && source != null && source.equals(bukkit);
    }

    @Override
    public int countGoals() {
        return this.goals.size();
    }

    @Override
    public Attribute getAttribute(AttributeKey key) {
        EntityPlayer mojang = this.getMojang();
        if (this.attributeMap == null) {
            try {
                Field mojangMap = EntityLiving.class.getDeclaredField("attributeMap");
                mojangMap.setAccessible(true);
                this.attributeMap = new TrackingAttributeMap((AttributeMapBase) mojangMap.get(mojang));
            } catch (ReflectiveOperationException ex) {
                this.game.getPlugin().getLogger().log(Level.SEVERE, "Could not inject attribute map", ex);
                return null;
            }
        }

        return this.attributeMap.getAttribute(key);
    }

    @Override
    public Color getColor() {
        return Color.ofChat(this.getChatColor());
    }

    @Override
    public Game getGame() {
        return this.game;
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
        return this.isOnline() ? this.getPlayer().getLocale() : DEFAULT_LOCALE;
    }

    @Override
    public ArcadePlayer getPlayer() {
        return this.player.get();
    }

    @Override
    public Set<GamePlayer> getPlayers() {
        return Collections.singleton(this);
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
    public boolean hasAnyGoals() {
        return !this.goals.isEmpty();
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
    public boolean isParticipating() {
        return this.canParticipate() && this.participating;
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
        this.send(ChatColor.YELLOW + message);
        this.sendAction(ChatColor.YELLOW + message);
    }

    @Override
    public void setParticipating(boolean participating) {
        if (this.canParticipate()) {
            this.participating = participating;
        }
    }

    public boolean canSee(GamePlayer target) {
        Game game = this.getGame();
        return this.isOnline() && target.isOnline() && game != null && game.getVisibility().canSee(this, target);
    }

    public BossBarFacet getBossBarFacet() {
        return this.bossBarFacet;
    }

    public Player getBukkit() {
        ArcadePlayer player = this.getPlayer();
        return player != null ? player.getBukkit() : null;
    }

    public ChatColor getChatColor() {
        return this.hasChatColor() ? this.chatColor : DEFAULT_CHAT_COLOR;
    }

    public ChatChannel getCurrentChannel() {
        return this.isOnline() ? this.channel : null;
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
        return this.isOnline() ? this.getPlayer().getFullName() : this.getDisplayName();
    }

    public PlayerLevel getLevel() {
        return this.isOnline() ? new PlayerLevel(this.getBukkit()) : PlayerLevel.ZERO;
    }

    public EntityPlayer getMojang() {
        return this.isOnline() ? this.getPlayer().getMojang() : null;
    }

    public boolean hasChatColor() {
        return this.chatColor != null;
    }

    public boolean hasDisplayName() {
        return this.isOnline() && (this.displayName != null ||
                this.getPlayer().getDisplayName() != null);
    }

    public boolean isDead() {
        return this.isOnline() && this.getBukkit().isDead();
    }

    public boolean isOnline() {
        return this.player != null;
    }

    public void kill() {
        if (this.isOnline() && !this.isDead()) {
            this.getBukkit().setHealth(0.0D);
        }
    }

    public void refresh() {
        if (!this.isOnline()) {
            return;
        }

        Player bukkit = this.getBukkit();
        boolean participating = this.isParticipating();
        if (!participating) {
            bukkit.leaveVehicle();
        }

        bukkit.setAffectsSpawning(participating);
        bukkit.setCollidesWithEntities(participating);
    }

    public void refreshVisibilityArcadePlayer(Iterable<ArcadePlayer> viewers) {
        List<GamePlayer> players = new ArrayList<>();
        for (ArcadePlayer viewer : viewers) {
            GamePlayer player = viewer.getGamePlayer();

            if (player != null && player.isOnline()) {
                players.add(player);
            }
        }

        this.refreshVisibility(players);
    }

    public void refreshVisibility(Iterable<GamePlayer> viewers) {
        if (!this.isOnline()) {
            return;
        }

        Player bukkit = this.getBukkit();
        for (GamePlayer viewer : viewers) {
            if (viewer.isOnline()) {
                Player viewerBukkit = viewer.getBukkit();

                // can this player see looped player?
                if (this.canSee(viewer)) {
                    bukkit.showPlayer(viewerBukkit);
                } else {
                    bukkit.hidePlayer(viewerBukkit);
                }

                // can looped player see this player?
                if (viewer.canSee(this)) {
                    viewerBukkit.showPlayer(bukkit);
                } else {
                    viewerBukkit.hidePlayer(bukkit);
                }
            }
        }
    }

    public void reset() {
        if (!this.isOnline()) {
            return;
        }

        Player bukkit = this.getBukkit();
        this.refresh();

        bukkit.closeInventory();
        this.getPlayer().clearInventory(true);

        this.resetHealth();
        this.setLevel(PlayerLevel.getDefaultLevel());

        bukkit.setAbsorption(AbsorptionContent.Config.DEFAULT_ABSORPTION);
        bukkit.setAllowFlight(CanFlyContent.Config.DEFAULT_CAN_FLY);
        bukkit.setArrowsStuck(RemoveArrowsContent.FINAL_COUNT);
        bukkit.setExhaustion(ExhaustionContent.Config.DEFAULT_LEVEL);
        bukkit.setExp((float) ExperienceContent.Config.DEFAULT_EXPERIENCE.getValue());
        bukkit.setFallDistance(FallDistanceContent.Config.DEFAULT_DISTANCE);
        bukkit.setFireTicks(TimeUtils.toTicksInt(BurnContent.Config.DEFAULT_TIME));
        bukkit.setFlying(FlyContent.Config.DEFAULT_FLY);
        bukkit.setFlySpeed(FlySpeedContent.Config.DEFAULT_SPEED);
        bukkit.setFoodLevel(HungerContent.Config.DEFAULT_LEVEL);
        bukkit.setGameMode(GameModeContent.Config.DEFAULT_GAME_MODE);
        bukkit.setGlowing(GlowContent.Config.DEFAULT_GLOW);
        bukkit.setGravity(GravityContent.Config.DEFAULT_GRAVITY);
        bukkit.setKnockbackReduction(KnockbackContent.Config.DEFAULT_KNOCKBACK);
        bukkit.setSaturation(SaturationContent.Config.DEFAULT_SATURATION);
        bukkit.setSilent(SilentContent.Config.DEFAULT_SILENT);
        bukkit.setSneaking(false);
        bukkit.setSprinting(false);
        bukkit.setVelocity(VelocityContent.Config.DEFAULT_VELOCITY);
        bukkit.setWalkSpeed(WalkSpeedContent.Config.DEFAULT_SPEED);

        bukkit.resetPlayerTime();
        bukkit.resetPlayerWeather();
        bukkit.resetTitle();

        for (PotionEffectType effect : PotionEffectType.values()) {
            bukkit.removePotionEffect(effect);
        }

        if (this.attributeMap != null) {
            for (AttributeKey key : this.attributeMap.getTracking()) {
                Attribute attribute = this.attributeMap.getAttribute(key);
                if (attribute != null) {
                    attribute.removeAllModifers();
                }
            }
            this.attributeMap.unsubscribeAll();
        }

        this.getMojang().reset();
    }

    public void resetDisplayName() {
        this.setDisplayName(null);
    }

    public void resetHealth() {
        this.getAttribute(MaxHealthContent.MAX_HEALTH).resetValue();

        Player bukkit = this.getBukkit();
        bukkit.setHealthScale(HealthScaleContent.Config.DEFAULT_SCALE);
        bukkit.setHealthScaled(false);
        bukkit.setHealth(HealthContent.Config.DEFAULT_HEALTH);
    }

    public boolean sendPacket(Object packet) {
        boolean isOnline = this.isOnline();
        if (isOnline) {
            this.getPlayer().sendPacket(packet);
        }

        return isOnline;
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

    public void setLevel(PlayerLevel level) {
        if (this.isOnline()) {
            this.getBukkit().setLevel(level.getLevel());
        }
    }

    public void setPlayer(ArcadePlayer player) {
        this.player = new WeakReference<>(player);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("uuid", this.uuid)
                .append("isOnline", this.isOnline())
                .append("username", this.username)
                .build();
    }

    /**
     * @deprecated Internal use only.
     */
    @Deprecated
    public final void onDisconnect(ArcadePlayer player) {
        this.attributeMap = null;
    }
}
