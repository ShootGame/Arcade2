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

import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.List;

public class RegionBounds extends CuboidRegion {
    private final Region region;

    public RegionBounds(Region region, Vector min, Vector max) {
        super(region.getGame(), "_" + region.getId() + "-bounds", min, max);

        this.region = region;
    }

    @Override
    public boolean contains(BlockVector vector) {
        return this.getRegion().contains(vector);
    }

    @Override
    public boolean contains(Region region) {
        return this.getRegion().contains(region);
    }

    @Override
    public boolean contains(Vector vector) {
        return this.getRegion().contains(vector);
    }

    @Override
    public List<Block> getBlocks() {
        return this.getRegion().getBlocks();
    }

    @Override
    public RegionBounds getBounds() {
        return this;
    }

    @Override
    protected RegionBounds createBounds() {
        return null;
    }

    public Region getRegion() {
        return this.region;
    }
}
