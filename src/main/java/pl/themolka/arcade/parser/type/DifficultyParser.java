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

import org.bukkit.Difficulty;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Set;

@Produces(Difficulty.class)
public class DifficultyParser extends ElementParser<Difficulty>
                              implements InstallableParser {
    private Parser<Integer> idParser;
    private Parser<Difficulty> difficultyParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.idParser = library.type(Integer.class);
        this.difficultyParser = library.enumType(Difficulty.class);
    }

    @Override
    public Set<Object> expect() {
        return this.difficultyParser.expect();
    }

    @Override
    protected Result<Difficulty> parseElement(Context context, Element element, String name, String value) throws ParserException {
        // Try to find legacy ID first.
        Integer id = this.idParser.parseWithDefinition(context, element, name, value).orNull();
        if (id != null) {
            Difficulty difficulty = Difficulty.getByValue(id);

            if (difficulty != null) {
                return Result.fine(element, name, value, difficulty);
            }
        }

        return this.difficultyParser.parseWithDefinition(context, element, name, value);
    }
}
