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

package pl.themolka.arcade.kit;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class KitsGame extends GameModule {
    private final Map<String, Kit> kits = new LinkedHashMap<>();

    protected KitsGame(Game game, IGameConfig.Library library, Config config) {
        for (Kit.Config kit : config.kits().get()) {
            this.kits.put(kit.id(), library.getOrDefine(game, kit));
        }
    }

    @Override
    public void onEnable() {
        this.insertContentFromInheritedKits();
    }

    public void addKit(Kit kit) {
        this.kits.put(kit.getId(), kit);
    }

    public Kit getKit(String id) {
        return this.getKit(id, null);
    }

    public Kit getKit(String id, Kit def) {
        return id != null ? this.kits.getOrDefault(id.trim(), def) : def;
    }

    public Set<String> getKitIds() {
        return this.kits.keySet();
    }

    public Collection<Kit> getKits() {
        return this.kits.values();
    }

    public void removeKit(Kit kit) {
        this.removeKit(kit.getId());
    }

    public void removeKit(String id) {
        this.kits.remove(id);
    }

    private void insertContentFromInheritedKits() {
        Collection<Kit> allKits = this.kits.values();
        for (Kit kit : allKits) {
            for (String inherit : kit.getInherit()) {
                for (Kit dependency : allKits) {
                    if (dependency.getId().equals(inherit)) {
                        kit.addContent(dependency);
                    }
                }
            }
        }
    }

    public interface Config extends IGameModuleConfig<KitsGame> {
        Ref<Set<Kit.Config>> kits();

        @Override
        default KitsGame create(Game game, Library library) {
            return new KitsGame(game, library, this);
        }
    }
}
