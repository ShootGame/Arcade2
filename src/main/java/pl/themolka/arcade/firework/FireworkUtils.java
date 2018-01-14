package pl.themolka.arcade.firework;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public final class FireworkUtils {
    private FireworkUtils() {
    }

    public static FireworkMeta createMeta(Firework firework, int power, FireworkEffect... effects) {
        return createMeta(firework.getFireworkMeta(), power, effects);
    }

    public static FireworkMeta createMeta(FireworkMeta meta, int power, FireworkEffect... effects) {
        meta.addEffects(effects);
        meta.setPower(power);
        return meta;
    }

    public static Firework spawn(Location at, int power, FireworkEffect... effects) {
        Firework firework = at.getWorld().spawn(at, Firework.class);
        firework.setFireworkMeta(createMeta(firework, power, effects));
        return firework;
    }
}
