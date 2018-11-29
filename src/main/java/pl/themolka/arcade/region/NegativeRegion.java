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

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;

import java.util.Random;

public class NegativeRegion extends AbstractRegion {
    private final UnionRegion region;

    public NegativeRegion(UnionRegion region) {
        this(region.getId(), region);
    }

    public NegativeRegion(String id, UnionRegion region) {
        super(region.getGame(), id);

        this.region = region;
    }

    public NegativeRegion(NegativeRegion original) {
        this(original.getId(), original.getRegion());
    }

    @Override
    public boolean contains(BlockVector vector) {
        return !this.getRegion().contains(vector);
    }

    @Override
    public boolean contains(Region region) {
        return !this.getRegion().contains(region);
    }

    @Override
    public boolean contains(Vector vector) {
        return !this.getRegion().contains(vector);
    }

    @Override
    public RegionBounds getBounds() {
        return this.getRegion().getBounds();
    }

    @Override
    public Vector getCenter() {
        return this.getRegion().getCenter();
    }

    @Override
    public double getHighestY() {
        return this.getRegion().getHighestY();
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        return this.getRegion().getRandomVector(random, limit);
    }

    public UnionRegion getRegion() {
        return this.region;
    }

    public interface Config extends AbstractRegion.Config<NegativeRegion> {
        Ref<UnionRegion.Config> region();

        @Override
        default NegativeRegion create(Game game, Library library) {
            UnionRegion.Config region = this.region().getIfPresent();
            if (region == null) {
                return null;
            }

            UnionRegion union = library.getOrDefine(game, region);
            if (union == null) {
                return null;
            }

            return new NegativeRegion(this.id(), union);
        }
    }
}
