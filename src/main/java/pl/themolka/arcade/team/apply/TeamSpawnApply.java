package pl.themolka.arcade.team.apply;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;

public abstract class TeamSpawnApply implements PlayerApplicable {
    public static final float DEFAULT_YAW = 0F;
    public static final float DEFAULT_PITCH = 0F;

    public static final PlayerTeleportEvent.TeleportCause TELEPORT_CAUSE =
            PlayerTeleportEvent.TeleportCause.PLUGIN;

    private float yaw = DEFAULT_YAW;
    private boolean customYaw = false;

    private float pitch = DEFAULT_PITCH;
    private boolean customPitch = false;

    public TeamSpawnApply() {
        this(DEFAULT_YAW);
    }

    public TeamSpawnApply(float yaw) {
        this(yaw, DEFAULT_PITCH);
    }

    public TeamSpawnApply(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().teleport(this.spawn(), TELEPORT_CAUSE);
    }

    public abstract Location getSpawnLocation();

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        this.customYaw = true;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        this.customPitch = true;
    }

    public Location spawn() {
        Location spawn;
        do {
            spawn = this.getSpawnLocation();
        } while (spawn == null);

        if (this.customYaw) {
            spawn.setYaw(this.getYaw());
        }

        if (this.customPitch) {
            spawn.setPitch(this.getPitch());
        }

        return spawn;
    }
}
