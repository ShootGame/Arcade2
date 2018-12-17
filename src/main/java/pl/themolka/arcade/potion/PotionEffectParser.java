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

import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.time.TimeUtils;

import java.util.Collections;
import java.util.Set;

@Produces(PotionEffect.class)
public class PotionEffectParser extends NodeParser<PotionEffect>
                                implements InstallableParser {
    private Parser<Boolean> ambientParser;
    private Parser<Integer> amplifierParser;
    private Parser<Color> colorParser;
    private Parser<Time> durationParser;
    private Parser<Boolean> particlesParser;
    private Parser<PotionEffectType> typeParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.ambientParser = library.type(Boolean.class);
        this.amplifierParser = library.type(Integer.class);
        this.colorParser = library.type(Color.class);
        this.durationParser = library.type(Time.class);
        this.particlesParser = library.type(Boolean.class);
        this.typeParser = library.type(PotionEffectType.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("potion effect");
    }

    @Override
    protected Result<PotionEffect> parsePrimitive(Context context, Node node, String name, String value) throws ParserException {
        boolean ambient = this.ambientParser.parse(context, node.property("ambient")).orDefault(true);
        int amplifier = this.amplifierParser.parse(context, node.property("amplifier")).orDefault(0);
        Color color = this.colorParser.parse(context, node.property("color")).orDefaultNull();
        Time duration = this.durationParser.parse(context, node.property("duration", "time")).orFail();
        boolean particles = this.particlesParser.parse(context, node.property("particles")).orDefault(true);
        PotionEffectType type = this.typeParser.parseWithDefinition(context, node, name, value).orFail();

        if (amplifier < 0) {
            throw this.fail(node, name, value, "Amplifier must be positive or zero (greater than, or equal to 0)");
        }

        return Result.fine(node, name, value, new PotionEffect(
                type, TimeUtils.toTicksInt(duration), amplifier, ambient, particles, color));
    }
}
