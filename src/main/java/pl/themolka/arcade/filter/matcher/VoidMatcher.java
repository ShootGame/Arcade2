package pl.themolka.arcade.filter.matcher;

import org.bukkit.Locatable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class VoidMatcher extends Matcher<Block> {
    public static final int VOID_LEVEL = 0;

    protected VoidMatcher() {
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof Block) {
            return this.matches((Block) object);
        } else if (object instanceof Location) {
            return this.matches((Location) object);
        } else if (object instanceof Locatable) {
            return this.matches((Locatable) object);
        }

        return false;
    }

    @Override
    public boolean matches(Block block) {
        return block != null && block.isEmpty();
    }

    public boolean matches(Location location) {
        return location != null && this.matches(location.getBlock());
    }

    public boolean matches(Locatable locatable) {
        return locatable != null && this.matches(locatable.getLocation());
    }

    public boolean matches(World world, double x, double z) {
        return world != null && this.matches(new Location(world, x, VOID_LEVEL, z));
    }

    public boolean matches(World world, int x, int z) {
        return this.matches(world, (double) x, (double) z);
    }

    @NestedParserName("void")
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config> {
        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            return ParserResult.fine(node, name, new Config() {});
        }
    }

    public interface Config extends Matcher.Config<VoidMatcher> {
        @Override
        default VoidMatcher create(Game game) {
            return new VoidMatcher();
        }
    }
}
