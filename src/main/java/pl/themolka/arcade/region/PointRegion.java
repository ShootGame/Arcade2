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

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PointRegion extends AbstractRegion {
    private final Vector point;
    private final List<Block> blocks;
    private final RegionBounds bounds;

    public PointRegion(Game game, String id, Vector point) {
        super(game, id);

        this.point = point;

        this.blocks = this.createBlocks();
        this.bounds = this.createBounds();
    }

    public PointRegion(PointRegion original) {
        this(original.getGame(), original.getId(), original.getPoint());
    }

    protected PointRegion(Game game, Config config) {
        this(game, config.id(), config.point().get());
    }

    @Override
    public boolean contains(BlockVector vector) {
        return this.containsZero(vector);
    }

    @Override
    public boolean contains(Region region) {
        return false;
    }

    @Override
    public boolean contains(Vector vector) {
        Vector point = this.getPoint();
        return  vector.getBlockX() == point.getBlockX() &&
                vector.getBlockY() == point.getBlockY() &&
                vector.getBlockZ() == point.getBlockZ();
    }

    @Override
    public List<Block> getBlocks() {
        return this.blocks;
    }

    @Override
    public RegionBounds getBounds() {
        return this.bounds;
    }

    @Override
    public Vector getCenter() {
        return this.getPoint();
    }

    @Override
    public double getHighestY() {
        return this.getPoint().getY();
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        return this.getPoint();
    }

    public Vector getPoint() {
        return this.point;
    }

    private List<Block> createBlocks() {
        Location location = this.getPoint().toLocation(this.getWorld());
        return Collections.singletonList(location.getBlock());
    }

    private RegionBounds createBounds() {
        return new RegionBounds(this, this.point, this.point);
    }

    public interface Config extends AbstractRegion.Config<PointRegion> {
        Ref<Vector> point();

        @Override
        default PointRegion create(Game game, Library library) {
            return new PointRegion(game, this);
        }
    }
}
