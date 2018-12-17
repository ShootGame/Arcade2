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

package pl.themolka.arcade.respawn;

import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserNotSupportedException;

@ModuleInfo(id = "Auto-Respawn")
public class AutoRespawnModule extends Module<AutoRespawnGame> {
    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserLibrary library) throws ParserNotSupportedException {
        return library.of(AutoRespawnGameParser.class);
    }
}
