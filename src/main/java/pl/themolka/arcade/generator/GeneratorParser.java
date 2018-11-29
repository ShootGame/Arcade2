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

package pl.themolka.arcade.generator;

import com.google.common.collect.ImmutableSet;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserMap;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.parser.Silent;

import java.util.Collections;
import java.util.Set;

@Produces(Generator.class)
public class GeneratorParser extends NodeParser<Generator>
                             implements InstallableParser {
    private static final Set<Class<?>> types = ImmutableSet.<Class<?>>builder()
            .add(GeneratorParser.class)
            .add(VanillaGenerator.class)
            .add(VoidGenerator.class)
            .build();

    private NestedParserMap<BaseGeneratorParser<?>> nested;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.nested = new NestedParserMap<>(context);
        for (Class<?> clazz : types) {
            this.nested.scan(clazz);
        }
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("world generator");
    }

    @Override
    protected Result<Generator> parsePrimitive(Node node, String name, String value) throws ParserException {
        BaseGeneratorParser<?> parser = this.nested.parse(value);
        if (parser == null) {
            throw this.fail(node, null, name, "Unknown world generator type");
        }

        return Result.fine(node, name, value, parser.parse(node).orFail());
    }

    @NestedParserName("default")
    @Produces(Generator.class)
    @Silent
    public static class Default extends BaseGeneratorParser<Generator> {
        @Override
        public Set<Object> expect() {
            return Collections.singleton("default generator");
        }

        @Override
        protected Result<Generator> parseNode(Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, value, Generator.DEFAULT_GENERATOR);
        }
    }
}
