package pl.themolka.arcade.session;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.FoodLevelContent;
import pl.themolka.arcade.kit.HealthContent;
import pl.themolka.arcade.kit.WalkSpeedContent;
import pl.themolka.arcade.metadata.Metadata;
import pl.themolka.arcade.metadata.MetadataContainer;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.permission.PermissionContext;
import pl.themolka.arcade.time.Time;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class ArcadePlayer implements Metadata, Sender {
    public static final long SOUND_INTERVAL = 500L; // half second

    private final ArcadePlugin plugin;

    private final transient Player bukkit; // Bukkit
    private transient GamePlayer gamePlayer;
    private Time lastPlayedSound;
    private final MetadataContainer metadata = new MetadataContainer();
    private final PermissionContext permissions;

    public ArcadePlayer(ArcadePlugin plugin, Player bukkit) {
        this.plugin = plugin;

        this.bukkit = bukkit;
        this.permissions = new PermissionContext(plugin, this);
    }

    @Override
    public GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    @Override
    public Object getMetadata(Class<? extends Module<?>> owner, String key, Object def) {
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
        return this.getBukkit().hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public void send(String message) {
        this.sendMessage(ChatMessageType.SYSTEM, message);
    }

    @Override
    public void sendAction(String action) {
        this.sendMessage(ChatMessageType.ACTION_BAR, action);
    }

    @Override
    public void sendChat(String chat) {
        this.sendMessage(ChatMessageType.CHAT, chat);
    }

    @Override
    public void setMetadata(Class<? extends Module<?>> owner, String key, Object metadata) {
        this.metadata.setMetadata(owner, key, metadata);
    }

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

    public String getDisplayName() {
        return this.bukkit.getDisplayName();
    }

    public Time getLastPlayedSound() {
        return this.lastPlayedSound;
    }

    public Locale getLocale() {
        return this.bukkit.getCurrentLocale();
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

    public void play(ArcadeSound sound, Location position, float volume) {
        this.play(sound.getSound(), position, volume);
    }

    public void play(Sound sound, Location position, float volume) {
        this.play(sound, position, volume, ArcadeSound.DEFAULT_PITCH);
    }

    public void play(ArcadeSound sound, Location position, float volume, float pitch) {
        this.play(sound.getSound(), position, volume, pitch);
    }

    public void play(Sound sound, Location position, float volume, float pitch) {
        Time now = Time.now();
        if (now.minus(this.getLastPlayedSound()).toMillis() < SOUND_INTERVAL) {
            return;
        }

        this.bukkit.playSound(position, sound, volume, pitch);
        this.lastPlayedSound = now;
    }

    public void reset() {
        this.getBukkit().setArrowsStuck(0);
        this.getBukkit().setExp(0);
        this.getBukkit().setFoodLevel(FoodLevelContent.DEFAULT_LEVEL);
        this.getBukkit().setGameMode(GameMode.CREATIVE);
        this.getBukkit().setHealthScale(HealthContent.DEFAULT_HEALTH);
        this.getBukkit().setHealth(HealthContent.DEFAULT_HEALTH);
        this.getBukkit().setWalkSpeed(WalkSpeedContent.DEFAULT_SPEED);

        this.getBukkit().resetPlayerTime();
        this.getBukkit().resetPlayerWeather();
        this.getBukkit().resetTitle();
    }

    public void resetFull() {
        this.clearInventory(true);
        this.resetDisplayName();

        for (PotionEffectType potion : PotionEffectType.values()) {
            this.bukkit.removePotionEffect(potion);
        }

        this.reset();
    }

    public void resetDisplayName() {
        this.setDisplayName(null);
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
}
