package pl.themolka.arcade.spawn;

import pl.themolka.arcade.config.IConfig;

public interface Directional {
    default org.bukkit.geometry.Direction createDirection() {
        return org.bukkit.geometry.Direction.of(this.getYaw(), getPitch());
    }

    default float getYaw() {
        return Config.DEFAULT_YAW;
    }

    default float getPitch() {
        return Config.DEFAULT_PITCH;
    }

    interface Config<T extends Directional> extends IConfig<T> {
        float DEFAULT_YAW = 180F; // north
        float DEFAULT_PITCH = 0F; // forward

        default float yaw() { return DEFAULT_YAW; };
        default float pitch() { return DEFAULT_PITCH; };
    }
}
