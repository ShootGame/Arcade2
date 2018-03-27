package pl.themolka.arcade.session;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.AttributeMapBase;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.attribute.Attributable;
import pl.themolka.arcade.attribute.Attribute;
import pl.themolka.arcade.attribute.AttributeKey;
import pl.themolka.arcade.attribute.AttributeMap;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.content.HealthContent;
import pl.themolka.arcade.kit.content.HealthScaleContent;
import pl.themolka.arcade.time.Time;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

/**
 * ArcadePlayers are created when player joins the server, and removed then he
 * quits it. ArcadePlayers should not store any game related data. ArcadePlayers
 * are not secure to be stored in objects - use {@link GamePlayer} or
 * {@link UUID} instead.
 */
public class ArcadePlayer implements Attributable, Sender {
    public static final long SOUND_INTERVAL = 500L; // half second
    public static final int USERNAME_MIN_LENGTH = 3; // was changed to 4 in beta
    public static final int USERNAME_MAX_LENGTH = 16;

    private static final ToStringStyle toStringStyle = ToStringStyle.NO_FIELD_NAMES_STYLE;

    private final ArcadePlugin plugin;

    private final Reference<Player> bukkit; // Bukkit
    private Reference<GamePlayer> gamePlayer; // NEVER null

    private AttributeMap attributeMap;
    private Time lastPlayedSound = Time.now();

    public ArcadePlayer(ArcadePlugin plugin, Player bukkit) {
        this.plugin = plugin;

        this.bukkit = new WeakReference<>(bukkit);
    }

    //
    // Inherited stuff
    //

    @Override
    public Attribute getAttribute(AttributeKey key) {
        EntityPlayer mojang = this.getMojang();
        if (this.attributeMap == null) {
            try {
                Field mojangMap = EntityLiving.class.getDeclaredField("attributeMap");
                mojangMap.setAccessible(true);
                this.attributeMap = new AttributeMap((AttributeMapBase) mojangMap.get(mojang));
            } catch (ReflectiveOperationException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not inject attribute map", ex);
                return null;
            }
        }

        return this.attributeMap.getAttribute(key);
    }

    @Override
    public GamePlayer getGamePlayer() {
        return this.gamePlayer.get();
    }

    @Override
    public Locale getLocale() {
        Locale locale = this.getBukkit().getCurrentLocale();
        if (locale != null) {
            return locale;
        }

        return DEFAULT_LOCALE;
    }

    @Override
    public ArcadePlayer getPlayer() {
        return this;
    }

    @Override
    public String getUsername() {
        return this.getBukkit().getName();
    }

    @Override
    public UUID getUuid() {
        return this.getBukkit().getUniqueId();
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.getBukkit().isOp() ||
                this.getBukkit().hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public void send(String message) {
        if (this.getChatState().message()) {
            this.sendMessage(ChatMessageType.SYSTEM, message);
        }
    }

    @Override
    public void sendAction(String action) {
        if (this.getChatState().action()) {
            this.sendMessage(ChatMessageType.ACTION_BAR, action);
        }
    }

    @Override
    public void sendChat(String chat) {
        if (this.getChatState().chat()) {
            this.sendMessage(ChatMessageType.CHAT, chat);
        }
    }

    //
    // Methods
    //

    public void clearInventory() {
        this.clearInventory(true);
    }

    public void clearInventory(boolean armor) {
        PlayerInventory inventory = this.getBukkit().getInventory();
        inventory.clear();

        if (armor) {
            inventory.setHelmet(null);
            inventory.setChestplate(null);
            inventory.setLeggings(null);
            inventory.setBoots(null);
        }
    }

    public Player getBukkit() {
        return this.bukkit.get();
    }

    public ChatState getChatState() {
        return ChatState.ofMojang(this.getMojang().getChatFlags());
    }

    public String getDisplayName() {
        String display = this.getBukkit().getDisplayName();
        if (display != null) {
            return display;
        }

        return this.getUsername();
    }

    public String getFullName() {
        return this.getDisplayName() + ChatColor.RESET;
    }

    public EntityPlayer getMojang() {
        return ((CraftPlayer) this.getBukkit()).getHandle();
    }

    public Time getLastPlayedSound() {
        return this.lastPlayedSound;
    }

    public void play(ArcadeSound sound) {
        this.play(sound.getSound());
    }

    public void play(Sound sound) {
        this.play(sound, this.getBukkit().getLocation());
    }

    public void play(ArcadeSound sound, Location position) {
        this.play(sound.getSound(), position);
    }

    public void play(Sound sound, Location position) {
        this.play(sound, position, ArcadeSound.DEFAULT_VOLUME);
    }

    public void play(ArcadeSound sound, Location position,
                     float volume) {
        this.play(sound.getSound(), position, volume);
    }

    public void play(Sound sound, Location position,
                     float volume) {
        this.play(sound, position, volume, ArcadeSound.DEFAULT_PITCH);
    }

    public void play(ArcadeSound sound, Location position,
                     float volume, float pitch) {
        this.play(sound.getSound(), position, volume, pitch);
    }

    public void play(Sound sound, Location position,
                     float volume, float pitch) {
        Time now = Time.now();
        if (now.minus(this.getLastPlayedSound()).toMillis() >= SOUND_INTERVAL) {
            this.getBukkit().playSound(position, sound, volume, pitch);
            this.lastPlayedSound = now;
        }
    }

    public void refresh() {
        if (this.getGamePlayer() != null) {
            this.getGamePlayer().refresh();
        }
    }

    public void resetDisplayName() {
        this.setDisplayName(null);
    }

    public void respawn() {
        this.respawn(null);
    }

    /**
     * @deprecated Will not fire {@link pl.themolka.arcade.respawn.PlayerRespawnEvent}
     * and {@link org.bukkit.event.player.PlayerRespawnEvent}!
     */
    @Deprecated
    public void respawn(Location at) {
        int worldId = -1; // 'worldId' is not used if 'at' is null
        if (at != null) {
            worldId = ((CraftWorld) at.getWorld()).getHandle().dimension;
        }

        Player bukkit = this.getBukkit();
        if (bukkit != null) {
            bukkit.setHealthScale(HealthScaleContent.Config.DEFAULT_SCALE);
            bukkit.setHealth(HealthContent.Config.DEFAULT_HEALTH);
        }

        EntityPlayer mojang = this.getMojang();
        if (mojang != null) {
            mojang.server.getPlayerList().moveToWorld(mojang, worldId, false, at, false);
        }
    }

    public void setDisplayName(String displayName) {
        this.getBukkit().setDisplayName(displayName);
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = new WeakReference<>(gamePlayer);
    }

    private void sendMessage(ChatMessageType type, String message) {
        this.getBukkit().sendMessage(type, TextComponent.fromLegacyText(message));
    }

    public void sendPacket(Object packet) {
        this.getMojang().playerConnection.sendPacket((Packet<?>) packet);
    }

    @Override
    public String toString() {
        Player bukkit = this.getBukkit();
        return new ToStringBuilder(this, toStringStyle)
                .append("uuid", bukkit.getUniqueId())
                .append("username", bukkit.getName())
                .build();
    }

    /**
     * Represents player's chat visibility.
     */
    public enum ChatState {
        /**
         * The chat is visible for all messages.
         */
        VISIBLE(true, true, true),

        /**
         * The chat is printing only server messages.
         */
        HIDDEN(true, true, false),

        /**
         * The chat is disabled and cannot print any messages.
         */
        DISABLED(false, true, false),
        ;

        private final boolean message;
        private final boolean action;
        private final boolean chat;

        ChatState(boolean message, boolean action, boolean chat) {
            this.message = message;
            this.action = action;
            this.chat = chat;
        }

        /**
         * Can this chat state print server messages?
         */
        public boolean message() {
            return this.message;
        }

        /**
         * Can this chat state print action messages?
         */
        public boolean action() {
            return this.action;
        }

        /**
         * Can this chat state print chat messages?
         */
        public boolean chat() {
            return this.chat;
        }

        /**
         * Convert Mojang's chat visibility to this enum value.
         * @param mojang Mojang's chat state enum representation.
         */
        static ChatState ofMojang(EntityHuman.EnumChatVisibility mojang) {
            if (mojang != null) {
                switch (mojang) {
                    case FULL:
                        return VISIBLE;
                    case SYSTEM:
                        return HIDDEN;
                    case HIDDEN:
                        return DISABLED;
                }
            }

            return VISIBLE; // defaults
        }
    }
}
