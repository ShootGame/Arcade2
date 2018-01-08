package pl.themolka.arcade.filter.matcher;

import org.bukkit.Locatable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import pl.themolka.arcade.filter.FilterResult;

@MatcherId("void")
public class VoidMatcher extends Matcher {
    public static int VOID_LEVEL = 0;

    @Override
    public FilterResult matches(Object object) {
        if (object instanceof Locatable) {
            return this.of(this.matches((Locatable) object));
        } else if (object instanceof Location) {
            return this.of(this.matches((Location) object));
        }

        return this.abstain();
    }

    public boolean matches(Locatable locatable) {
        return this.matches(locatable.getLocation());
    }

    public boolean matches(Location location) {
        return this.matches(location.getWorld(), location.getX(), location.getZ());
    }

    public boolean matches(World world, double x, double z) {
        return this.matches(world, (int) x, (int) z);
    }

    public boolean matches(World world, int x, int z) {
        return world.getBlockAt(x, VOID_LEVEL, z).getType().equals(Material.AIR);
    }
}
