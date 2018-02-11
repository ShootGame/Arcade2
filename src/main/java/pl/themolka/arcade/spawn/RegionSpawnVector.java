package pl.themolka.arcade.spawn;

import org.bukkit.util.Vector;
import pl.themolka.arcade.region.Region;

public class RegionSpawnVector extends AbstractSpawnVector {
    public static final int RANDOM_LIMIT = Integer.MAX_VALUE;

    private final Region region;

    private float yaw;
    private float pitch;

    public RegionSpawnVector(Region region) {
        super(region.getWorld());
        this.region = region;

        this.resetYaw();
        this.resetPitch();
    }

    @Override
    public Vector getVector() {
        return this.region.getRandomVector(RANDOM_LIMIT);
    }

    @Override
    public float getYaw() {
        return this.yaw;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }

    public void resetYaw() {
        this.yaw = DEFAULT_YAW;
    }

    public void resetPitch() {
        this.pitch = DEFAULT_PITCH;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
