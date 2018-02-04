package pl.themolka.arcade.session;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet;
import net.minecraft.server.PacketPlayInClientCommand;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.HealthContent;
import pl.themolka.arcade.kit.MaxHealthContent;
import pl.themolka.arcade.metadata.Metadata;
import pl.themolka.arcade.metadata.MetadataContainer;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.permission.PermissionContext;
import pl.themolka.arcade.time.Time;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * ArcadePlayers are created when player joins the server, and removed then he
 * quits it. ArcadePlayers should not store any game related data. ArcadePlayers
 * are not secure to be stored in objects - use {@link GamePlayer} or
 * {@link UUID} instead.
 */
public class ArcadePlayer implements Metadata, Sender {
    public static final long SOUND_INTERVAL = 500L; // half second
    public static final int USERNAME_MIN_LENGTH = 3; // was changed to 4 in beta
    public static final int USERNAME_MAX_LENGTH = 16;

    private static final ToStringStyle toStringStyle = ToStringStyle.NO_FIELD_NAMES_STYLE;

    private final ArcadePlugin plugin;

    private final Player bukkit; // Bukkit
    private GamePlayer gamePlayer; // NEVER null
    private Time lastPlayedSound = Time.now();
    private final MetadataContainer metadata = new MetadataContainer();
    private final PermissionContext permissions;

    public ArcadePlayer(ArcadePlugin plugin, Player bukkit) {
        this.plugin = plugin;

        this.bukkit = bukkit;
        this.permissions = new PermissionContext(plugin, this);
    }

    //
    // Inherited stuff
    //

    @Override
    public GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    @Override
    public Locale getLocale() {
        Locale locale = this.bukkit.getCurrentLocale();
        if (locale != null) {
            return locale;
        }

        return DEFAULT_LOCALE;
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

    @Override
    public void setMetadata(Class<? extends Module<?>> owner,
                            String key, Object metadata) {
        this.metadata.setMetadata(owner, key, metadata);
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
        return this.bukkit;
    }

    public ChatState getChatState() {
        return ChatState.ofMojang(this.getMojang().getChatFlags());
    }

    public String getDisplayName() {
        String display = this.bukkit.getDisplayName();
        if (display != null) {
            return display;
        }

        return this.getUsername();
    }

    public String getFullName() {
        return this.getPermissions().getPrefixes() +
                this.getDisplayName() + ChatColor.RESET;
    }

    public EntityPlayer getMojang() {
        return ((CraftPlayer) this.getBukkit()).getHandle();
    }

    public Time getLastPlayedSound() {
        return this.lastPlayedSound;
    }

    public PermissionContext getPermissions() {
        return this.permissions;
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
            this.bukkit.playSound(position, sound, volume, pitch);
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
        Player bukkit = this.getBukkit();
        if (bukkit != null) {
            bukkit.setMaxHealth(MaxHealthContent.DEFAULT_HEALTH);
            bukkit.setHealthScale(MaxHealthContent.DEFAULT_HEALTH);
            bukkit.setHealth(HealthContent.DEFAULT_HEALTH);
        }

        EntityPlayer mojang = this.getMojang();
        if (mojang != null) {
            this.getMojang().playerConnection.a(new PacketPlayInClientCommand(
                    PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
        }
    }

    public void setDisplayName(String displayName) {
        this.bukkit.setDisplayName(displayName);
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    private void sendMessage(ChatMessageType type, String message) {
        this.bukkit.sendMessage(type, TextComponent.fromLegacyText(message));
    }

    public void sendPacket(Object packet) {
        this.getMojang().playerConnection.sendPacket((Packet<?>) packet);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, toStringStyle)
                .append("uuid", this.bukkit.getUniqueId())
                .append("username", this.bukkit.getName())
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
