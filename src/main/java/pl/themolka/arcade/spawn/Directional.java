package pl.themolka.arcade.spawn;

public interface Directional {
    float DEFAULT_YAW = 180F; // north
    float DEFAULT_PITCH = 0F; // forward

    default org.bukkit.geometry.Direction createDirection() {
        return org.bukkit.geometry.Direction.of(this.getYaw(), getPitch());
    }

    default float getYaw() {
        return DEFAULT_YAW;
    }

    default float getPitch() {
        return DEFAULT_PITCH;
    }
}
