/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.filter.matcher;

import org.bukkit.Locatable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

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
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, new Config() {});
        }
    }

    public interface Config extends Matcher.Config<VoidMatcher> {
        @Override
        default VoidMatcher create(Game game, Library library) {
            return new VoidMatcher();
        }
    }
}
