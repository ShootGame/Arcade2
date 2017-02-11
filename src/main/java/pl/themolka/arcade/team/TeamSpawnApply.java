package pl.themolka.arcade.team;

import org.bukkit.Location;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;

public abstract class TeamSpawnApply implements PlayerApplicable {
    private float yaw;
    private float pitch;

    public TeamSpawnApply() {
        this(0F);
    }

    public TeamSpawnApply(float yaw) {
        this(yaw, 0F);
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
        player.getBukkit().teleport(spawn);
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
