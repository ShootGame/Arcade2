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

package pl.themolka.arcade.module;

import pl.themolka.arcade.config.ConfigContext;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserNotSupportedException;

public class SimpleGlobalModule extends Module<GameModule> {
    @Override
    public final GameModuleParser<?, ?> getGameModuleParser(ParserLibrary library) throws ParserNotSupportedException {
        return null;
    }

    @Override
    public final void defineGameModule(Game game, IGameModuleConfig<GameModule> config, IGameConfig.Library library, ConfigContext context) {
    }

    @Override
    public final GameModule createGameModule(Game game, IGameModuleConfig<GameModule> config, IGameConfig.Library library, ConfigContext context) {
        return null;
    }
}
