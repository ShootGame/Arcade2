package pl.themolka.arcade.filter.matcher;

import org.bukkit.Locatable;
import org.bukkit.Location;
import org.bukkit.World;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.FilterResult;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;

@MatcherId("void")
public class VoidMatcher extends Matcher {
    public static final MatcherParser PARSER = new VoidParser();

    public static int VOID_LEVEL = 0;

    @Override
    public FilterResult matches(Object object) {
        if (object instanceof Location) {
            return this.of(this.matches((Location) object));
        } else if (object instanceof Locatable) {
            return this.of(this.matches((Locatable) object));
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
        return world.getBlockAt(x, VOID_LEVEL, z).isEmpty();
    }
}

class VoidParser implements MatcherParser<VoidMatcher> {
    @Override
    public VoidMatcher parse(Node node, ParserContext context) throws ParserException {
        return new VoidMatcher();
    }
}
