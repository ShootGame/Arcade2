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
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegionsGame extends GameModule {
    private final Set<Region> regions = new LinkedHashSet<>();
    private final Map<String, Region> regionsById = new HashMap<>();

    protected RegionsGame(Game game, IGameConfig.Library library, Config config) {
        for (AbstractRegion.Config<?> region : config.regions().get()) {
            this.addRegion(library.getOrDefine(game, region));
        }
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        register.add(new RegionListeners(this));
        return register;
    }

    public void addRegion(Region region) {
        this.regions.add(region);
        this.regionsById.put(region.getId(), region);
    }

    public Region getRegion(String id) {
        return this.getRegion(id, null);
    }

    public Region getRegion(String id, Region def) {
        return this.regionsById.getOrDefault(id, def);
    }

    public Set<String> getRegionIds() {
        return this.regionsById.keySet();
    }

    public Set<Region> getRegions() {
        return new LinkedHashSet<>(this.regions);
    }

    public void removeRegion(Region region) {
        this.removeRegion(region.getId());
    }

    public void removeRegion(String id) {
        this.regions.remove(this.getRegion(id));
        this.regionsById.remove(id);
    }

    //
    // Fetching regions
    //

    public Region fetch(Block block) {
        return this.fetch(block.getLocation().toBlockVector());
    }

    public Region fetch(BlockVector vector) {
        return this.fetch(new Location(this.getGame().getWorld(), vector.getX() + 0.5, vector.getY() + 0.5, vector.getZ() + 0.5));
    }

    public Region fetch(Entity entity) {
        return this.fetch(entity.getLocation());
    }

    public Region fetch(Location location) {
        return this.fetch(location.toVector());
    }

    public Region fetch(Vector vector) {
        List<Region> results = new ArrayList<>();
        for (Region region : this.regions) {
            if (region.contains(vector)) {
                results.add(region);
            }
        }

        if (results.isEmpty()) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        }

        return new UnionRegion(this.getGame(), results.toArray(new Region[results.size()]));
    }

    public Region fetch(double x, double z) {
        return this.fetch(x, Region.MIN_HEIGHT, z);
    }

    public Region fetch(double x, double y, double z) {
        return this.fetch(new Location(this.getGame().getWorld(), x, y, z));
    }

    public Region fetch(int x, int z) {
        return this.fetch(x, Region.MIN_HEIGHT, z);
    }

    public Region fetch(int x, int y, int z) {
        return this.fetch(new BlockVector(x, y, z));
    }

    public interface Config extends IGameModuleConfig<RegionsGame> {
        Ref<Set<AbstractRegion.Config<?>>> regions();

        @Override
        default RegionsGame create(Game game, Library library) {
            return new RegionsGame(game, library, this);
        }
    }
}
