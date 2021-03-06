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

package pl.themolka.arcade.region;

import org.apache.commons.lang3.RandomUtils;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import pl.themolka.arcade.game.Game;

import java.util.List;
import java.util.Random;

public class GlobalRegion extends CuboidRegion {
    public static final String REGION_ID = "_global";

    public GlobalRegion(Game game) {
        this(game, REGION_ID);
    }

    public GlobalRegion(Game game, String id) {
        super(game, id,
                new Vector(Double.MIN_VALUE, MIN_HEIGHT, Double.MIN_VALUE),
                new Vector(Double.MAX_VALUE, MAX_HEIGHT, Double.MAX_VALUE));
    }

    public GlobalRegion(CuboidRegion original) {
        this(original.getGame(), original.getId());
    }

    protected GlobalRegion(Game game, Config config) {
        this(game, config.id());
    }

    @Override
    public boolean contains(Region region) {
        return true;
    }

    @Override
    public boolean contains(Vector vector) {
        return true;
    }

    @Override
    public List<Block> getBlocks() {
        throw new UnsupportedOperationException("Cannot get blocks from the global region.");
    }

    @Override
    public RegionBounds getBounds() {
        return null;
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        return new Vector(random.nextDouble(), RandomUtils.nextDouble(MIN_HEIGHT, MAX_HEIGHT), random.nextDouble());
    }

    public interface Config extends AbstractRegion.Config<GlobalRegion> {
        default String id() { return REGION_ID; }

        @Override
        default GlobalRegion create(Game game, Library library) {
            return new GlobalRegion(game);
        }
    }
}
