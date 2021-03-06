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

import pl.themolka.arcade.config.ConfigContext;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

@ModuleInfo(id = "Regions",
        loadBefore = {
                FiltersModule.class})
public class RegionsModule extends Module<RegionsGame> {
    @Override
    public void defineGameModule(Game game, IGameModuleConfig<RegionsGame> config, IGameConfig.Library library, ConfigContext context) {
        context.define(GlobalRegion.REGION_ID, new GlobalRegion.Config() {});
    }
}
