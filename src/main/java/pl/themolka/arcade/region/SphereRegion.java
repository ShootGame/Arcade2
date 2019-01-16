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

public class SphereRegion extends AbstractRegion {
    private final RegionBounds bounds;
    private final Vector center;
    private final double radius;

    public SphereRegion(Game game, String id, Vector center, double radius) {
        super(game, id);

        this.center = center;
        this.radius = radius;

        this.bounds = this.createBounds();
    }

    public SphereRegion(SphereRegion original) {
        this(original.getGame(), original.getId(), original.getCenter(), original.getRadius());
    }

    protected SphereRegion(Game game, Config config) {
        this(game, config.id(), config.center().get(), config.radius().get());
    }

    @Override
    public boolean contains(BlockVector vector) {
        return this.containsRound(vector);
    }

    @Override
    public boolean contains(Region region) {
        return false;
    }

    @Override
    public boolean contains(Vector vector) {
        return vector.isInSphere(this.getCenter(), this.getRadius());
    }

    @Override
    public RegionBounds getBounds() {
        return this.bounds;
    }

    @Override
    public Vector getCenter() {
        return this.center;
    }

    @Override
    public double getHighestY() {
        return this.getCenter().getY() + this.getRadius();
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        for (int i = 0; i < limit; i++) {
            Vector vector = this.getBounds().getRandomVector(random, limit);
            if (vector != null && this.contains(vector)) {
                return vector;
            }
        }

        return null;
    }

    public double getRadius() {
        return this.radius;
    }

    private RegionBounds createBounds() {
        double radius = this.getRadius();
        Vector center = this.getCenter();
        return new RegionBounds(this,
                center.clone().subtract(new Vector(radius, radius, radius)),
                center.clone().add(new Vector(radius, radius, radius)));
    }

    public interface Config extends AbstractRegion.Config<SphereRegion> {
        Ref<Vector> center();
        Ref<Double> radius();

        @Override
        default SphereRegion create(Game game, Library library) {
            return new SphereRegion(game, this);
        }
    }
}
