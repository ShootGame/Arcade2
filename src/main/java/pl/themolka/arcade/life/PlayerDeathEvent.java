package pl.themolka.arcade.life;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.PlayerEvent;
import pl.themolka.arcade.time.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PlayerDeathEvent extends PlayerEvent {
    public static final Time DEFAULT_AUTO_RESPAWN_COOLDOWN = Time.SECOND;

    private final GamePlayer killer;
    private String deathMessage;
    private int dropExp;
    private final List<ItemStack> dropItems;
    private boolean keepInventory;
    private boolean keepLevel;
    private int newExp;
    private int newLevel;
    private Time autoRespawn = null;

    public PlayerDeathEvent(ArcadePlugin plugin, ArcadePlayer player, GamePlayer killer,
                            String deathMessage, int dropExp, List<ItemStack> dropItems) {
        super(plugin, player);

        this.killer = killer;
        this.deathMessage = deathMessage;
        this.dropExp = dropExp;
        this.dropItems = dropItems;
    }

    public boolean addDropItem(ItemStack item) {
        return this.dropItems.add(item);
    }

    public boolean addDropItems(Collection<ItemStack> items) {
        return this.dropItems.addAll(items);
    }

    public boolean addDropItems(ItemStack... items) {
        return this.addDropItems(Arrays.asList(items));
    }

    public void clearDropItems() {
        this.dropItems.clear();
    }

    public GamePlayer getVictim() {
        return this.getGamePlayer();
    }

    public Player getVictimBukkit() {
        return this.getVictim().getBukkit();
    }

    public GamePlayer getKiller() {
        return this.killer;
    }

    public Player getKillerBukkit() {
        GamePlayer killer = this.getKiller();
        return killer != null ? killer.getBukkit() : null;
    }

    public String getDeathMessage() {
        return this.deathMessage;
    }

    public int getDropExp() {
        return this.dropExp;
    }

    public List<ItemStack> getDropItems() {
        return new ArrayList<>(this.dropItems);
    }

    public int getNewExp() {
        return this.newExp;
    }

    public int getNewLevel() {
        return this.newLevel;
    }

    public boolean hasDeathMessage() {
        return this.deathMessage != null;
    }

    public boolean hasKiller() {
        return this.killer != null;
    }

    public boolean removeDropItem(ItemStack item) {
        return this.dropItems.remove(item);
    }

    public boolean removeDropItems(Collection<ItemStack> items) {
        return this.dropItems.removeAll(items);
    }

    public boolean removeDropItems(ItemStack... items) {
        return this.removeDropItems(Arrays.asList(items));
    }

    public void setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
    }

    public void setDropExp(int dropExp) {
        this.dropExp = dropExp;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public void setKeepLevel(boolean keepLevel) {
        this.keepLevel = keepLevel;
    }

    public void setNewExp(int newExp) {
        this.newExp = newExp;
    }

    public void setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }

    public boolean shouldKeepInventory() {
        return this.keepInventory;
    }

    public boolean shouldKeepLevel() {
        return this.keepLevel;
    }

    public boolean willDropItem(ItemStack item) {
        return this.dropItems.contains(item);
    }

    //
    // Auto Respawn
    //

    public Time getAutoRespawnCooldown() {
        return this.autoRespawn;
    }

    public boolean willAutoRespawn() {
        return validCooldown(this.autoRespawn);
    }

    public void setAutoRespawn(boolean autoRespawn) {
        this.setAutoRespawn(autoRespawn, DEFAULT_AUTO_RESPAWN_COOLDOWN);
    }

    public void setAutoRespawn(boolean autoRespawn, Time cooldown) {
        this.autoRespawn = autoRespawn && validCooldown(cooldown) ? cooldown : null;
    }

    private static boolean validCooldown(Time test) {
        return test != null && !test.isZero() && !test.isForever() && test.isPositive();
    }
}
