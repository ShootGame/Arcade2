package pl.themolka.arcade.spawn;

import pl.themolka.arcade.xml.XMLLocation;

public interface Directional {
    float DEFAULT_YAW = XMLLocation.YAW;
    float DEFAULT_PITCH = XMLLocation.PITCH;

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
