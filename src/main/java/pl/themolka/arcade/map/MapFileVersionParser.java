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

package pl.themolka.arcade.map;

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
import pl.themolka.arcade.util.versioning.SemanticVersion;

import java.util.Collections;
import java.util.Set;

@Produces(MapFileVersion.class)
public class MapFileVersionParser extends ElementParser<MapFileVersion>
                                  implements InstallableParser {
    private Parser<SemanticVersion> semanticVersionParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.semanticVersionParser = library.type(SemanticVersion.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("semantic map file version (read " + MapFileVersion.MANUAL + ")");
    }

    @Override
    protected Result<MapFileVersion> parseElement(Context context, Element element, String name, String value) throws ParserException {
        SemanticVersion semantic = this.semanticVersionParser.parseWithDefinition(context, element, name, value).orFail();
        return Result.fine(element, name, value, MapFileVersion.convertFrom(semantic));
    }
}
