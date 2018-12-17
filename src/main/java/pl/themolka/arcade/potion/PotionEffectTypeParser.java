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

package pl.themolka.arcade.potion;

import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(PotionEffectType.class)
public class PotionEffectTypeParser extends ElementParser<PotionEffectType> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("potion effect type");
    }

    @Override
    protected Result<PotionEffectType> parseElement(Context context, Element element, String name, String value) throws ParserException {
        PotionEffectType type = PotionEffectType.getByName(this.normalizePotionEffectName(value));
        if (type == null) {
            throw this.fail(element, name, value, "Unknown potion effect type");
        }

        return Result.fine(element, name, value, type);
    }

    protected String normalizePotionEffectName(String input) {
        return EnumParser.toEnumValue(input);
    }
}
