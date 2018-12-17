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

package pl.themolka.arcade.objective;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserMap;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(Objective.Config.class)
public class ObjectiveParser extends ConfigParser<Objective.Config<?>>
                             implements InstallableParser {
    private NestedParserMap<ObjectiveManifest.ObjectiveParser<?>> parsers;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);

        this.parsers = new NestedParserMap<>(library);
        for (ObjectiveManifest manifest : ObjectiveManifest.manifests) {
            ObjectiveManifest.ObjectiveParser<?> parser = manifest.defineParser(library);
            if (parser != null) {
                this.parsers.scanDedicatedClass(parser.getClass());
            }
        }
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("objective");
    }

    @Override
    protected Result<Objective.Config<?>> parseNode(Context context, Node node, String name, String value) throws ParserException {
        ObjectiveManifest.ObjectiveParser<?> parser = this.parsers.parse(name);
        if (parser == null) {
            throw this.fail(node, name, value, "Unknown objective type");
        }

        return Result.fine(node, name, value, parser.parseWithDefinition(context, node, name, value).orFail());
    }
}
