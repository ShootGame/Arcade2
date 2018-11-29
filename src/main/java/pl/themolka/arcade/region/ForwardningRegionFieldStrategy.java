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
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.util.Forwarding;

public abstract  class ForwardningRegionFieldStrategy extends Forwarding<IRegionFieldStrategy>
                                                      implements IRegionFieldStrategy {
    @Override
    public boolean regionContains(Region region, Location location) {
        return this.delegate().regionContains(region, location);
    }

    @Override
    public boolean regionContains(Region region, BlockVector vector) {
        return this.delegate().regionContains(region, vector);
    }

    @Override
    public boolean regionContains(Region region, Vector vector) {
        return this.delegate().regionContains(region, vector);
    }
}
