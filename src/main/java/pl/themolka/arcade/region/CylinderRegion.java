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

public class CylinderRegion extends AbstractRegion {
    private final RegionBounds bounds;
    private final Vector center;
    private final double radius;
    private final double height;

    public CylinderRegion(Game game, String id, Vector center, double radius, double height) {
        super(game, id);

        this.center = center;
        this.radius = radius;
        this.height = height;

        this.bounds = this.createBounds();
    }

    public CylinderRegion(CylinderRegion original) {
        this(original.getGame(), original.getId(), original.getCenter(), original.getRadius(), original.getHeight());
    }

    protected CylinderRegion(Game game, Config config) {
        this(game, config.id(), config.center().get(), config.radius().get(), config.height().get());
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
        Vector center = this.getCenter();
        double power = Math.pow(vector.getX() - center.getX(), 2) + Math.pow(vector.getZ() - center.getZ(), 2);

        if (!this.isYPresent(vector)) {
            return power <= Math.pow(this.getRadius(), 2);
        }

        return center.getY() <= vector.getY() && this.getHighestY() >= vector.getBlockY() && power <= Math.pow(this.getRadius(), 2);
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
        return this.getCenter().getY() + this.getHeight();
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

    public double getDiameter() {
        return this.getRadius() * 2;
    }

    public double getHeight() {
        return this.height;
    }

    private RegionBounds createBounds() {
        double radius = this.getRadius();
        Vector center = this.getCenter();
        return new RegionBounds(this,
                center.clone().subtract(new Vector(radius, 0, radius)),
                center.clone().add(new Vector(radius, this.getHeight(), radius)));
    }

    public interface Config extends AbstractRegion.Config<CylinderRegion> {
        Ref<Vector> center();
        Ref<Double> radius();
        default Ref<Double> height() { return Ref.ofProvided(Region.MAX_HEIGHT); }

        @Override
        default CylinderRegion create(Game game, Library library) {
            return new CylinderRegion(game, this);
        }
    }
}
