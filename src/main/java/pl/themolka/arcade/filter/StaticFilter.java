package pl.themolka.arcade.filter;

import org.bukkit.Locatable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public abstract class StaticFilter extends AbstractFilter {
    public static final StaticFilter ALLOW = new AllowFilter();
    public static final StaticFilter DENY = new DenyFilter();
    public static final StaticFilter VOID = new VoidFilter();

    private static class AllowFilter extends StaticFilter {
        @Override
        public FilterResult filter(Object object) {
            return FilterResult.ALLOW;
        }
    }

    private static class DenyFilter extends StaticFilter {
        @Override
        public FilterResult filter(Object object) {
            return FilterResult.DENY;
        }
    }

    private static class VoidFilter extends StaticFilter {
        public static int VOID_LEVEL = 0;

        @Override
        public FilterResult filter(Object object) {
            if (object instanceof Locatable) {
                return FilterResult.of(this.matches((Locatable) object));
            } else if (object instanceof Location) {
                return FilterResult.of(this.matches((Location) object));
            }

            return FilterResult.ABSTAIN;
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
}
