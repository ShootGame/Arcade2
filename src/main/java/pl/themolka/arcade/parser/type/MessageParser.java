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

import org.bukkit.ChatColor;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(String.class)
public class MessageParser extends ElementParser<String> {
    public static final char COLOR_CODE = '`';

    @Override
    public Set<Object> expect() {
        return Collections.singleton("colored text");
    }

    @Override
    protected Result<String> parseElement(Element element, String name, String value) throws ParserException {
        return Result.fine(element, name, value, this.color(value) + ChatColor.RESET);
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes(COLOR_CODE, text);
    }
}
