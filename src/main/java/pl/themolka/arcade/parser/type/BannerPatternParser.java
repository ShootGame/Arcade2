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

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
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

@Produces(Pattern.class)
public class BannerPatternParser extends NodeParser<Pattern>
                                 implements InstallableParser {
    private Parser<PatternType> typeParser;
    private Parser<DyeColor> colorParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.typeParser = library.type(PatternType.class);
        this.colorParser = library.type(DyeColor.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("banner pattern");
    }

    @Override
    protected Result<Pattern> parsePrimitive(Context context, Node node, String name, String value) throws ParserException {
        PatternType type = this.typeParser.parseWithDefinition(context, node, name, value).orFail();
        DyeColor color = this.colorParser.parse(context, node.property("color")).orFail();
        return Result.fine(node, name, value, new Pattern(color, type));
    }
}
