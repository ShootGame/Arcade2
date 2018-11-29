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

package pl.themolka.arcade.parser.type;

import org.bukkit.GameMode;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Set;

@Produces(GameMode.class)
public class GameModeParser extends ElementParser<GameMode>
                            implements InstallableParser {
    private Parser<Integer> idParser;
    private Parser<GameMode> gameModeParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.idParser = context.type(Integer.class);
        this.gameModeParser = context.enumType(GameMode.class);
    }

    @Override
    public Set<Object> expect() {
        return this.gameModeParser.expect();
    }

    @Override
    protected Result<GameMode> parseElement(Element element, String name, String value) throws ParserException {
        // Try to find legacy ID first.
        Integer id = this.idParser.parseWithDefinition(element, name, value).orNull();
        if (id != null) {
            GameMode gameMode = GameMode.getByValue(id);

            if (gameMode != null) {
                return Result.fine(element, name, value, gameMode);
            }
        }

        return this.gameModeParser.parseWithDefinition(element, name, value);
    }
}
