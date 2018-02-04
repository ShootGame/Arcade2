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

    private float yaw;
    private float pitch;

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
        Location spawn;
        do {
            spawn = this.getSpawnLocation();
        } while (spawn == null);

        spawn.setYaw(this.getYaw());
        spawn.setPitch(this.getPitch());

        player.getBukkit().teleport(spawn, TELEPORT_CAUSE);
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
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
