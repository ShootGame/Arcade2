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

package pl.themolka.arcade.firework;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
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

import java.util.Collections;
import java.util.Set;

@Produces(FireworkEffect.class)
public class FireworkEffectParser extends NodeParser<FireworkEffect>
                                  implements InstallableParser {
    private Parser<FireworkEffect.Type> typeParser;
    private Parser<Boolean> flickerParser;
    private Parser<Boolean> trailParser;
    private Parser<Color> colorParser;
    private Parser<Color> fadeColorParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.typeParser = library.type(FireworkEffect.Type.class);
        this.flickerParser = library.type(Boolean.class);
        this.trailParser = library.type(Boolean.class);
        this.colorParser = library.type(Color.class);
        this.fadeColorParser = library.type(Color.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("firework effect");
    }

    @Override
    protected Result<FireworkEffect> parseTree(Context context, Node node, String name) throws ParserException {
        FireworkEffect.Builder builder = FireworkEffect.builder()
                .with(this.typeParser.parse(context, node.property("type", "of")).orDefault(FireworkEffect.Type.BALL))
                .flicker(this.flickerParser.parse(context, node.property("flicker")).orDefault(false))
                .trail(this.trailParser.parse(context, node.property("trail")).orDefault(false));

        boolean colorsDefined = false;
        for (Node color : node.children("color")) {
            builder.withColor(this.colorParser.parse(context, color).orFail());
            colorsDefined = true;
        }

        if (!colorsDefined) {
            throw this.fail(node, name, null, "No colors defined");
        }

        for (Node fadeColor : node.children("fade-color", "fadecolor", "fade")) {
            builder.withFade(this.fadeColorParser.parse(context, fadeColor).orFail());
        }

        return Result.fine(node, name, builder.build());
    }
}
