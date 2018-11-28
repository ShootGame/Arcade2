package pl.themolka.arcade.firework;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

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

    public static Firework spawn(Plugin plugin, Location at, int power, FireworkEffect... effects) {
        return spawn(plugin, at, power, false, effects);
    }

    public static Firework spawn(Plugin plugin, Location at, int power, boolean damage, FireworkEffect... effects) {
        Firework firework = at.getWorld().spawn(at, Firework.class);
        firework.setFireworkMeta(createMeta(firework, power, effects));
        firework.setMetadata(DamageMetadata.KEY, new DamageMetadata(plugin, damage));
        return firework;
    }

    public static class DamageMetadata extends FixedMetadataValue {
        public static final String KEY = "fireworkGivesDamage";

        public DamageMetadata(Plugin owningPlugin, boolean value) {
            super(owningPlugin, value);
        }

        @Override
        public Boolean value() {
            return (boolean) super.value();
        }
    }
}
