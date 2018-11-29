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

package pl.themolka.arcade.parser;

import pl.themolka.arcade.util.Container;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ParserContainer implements Container<Parser> {
    private final Map<Class<? extends Parser>, Parser<?>> parsers = new LinkedHashMap<>();

    public ParserContainer() {
        this.parsers.put(TextParser.class, new TextParser()); // text is default
        this.parsers.put(IdParser.class, new IdParser());
    }

    @Override
    public Class<Parser> getType() {
        return Parser.class;
    }

    public boolean contains(Class<? extends Parser> clazz) {
        return this.parsers.containsKey(clazz);
    }

    public boolean containsParser(Parser<?> parser) {
        return this.parsers.containsValue(parser);
    }

    public IdParser getIdParser() throws ParserNotSupportedException {
        return this.getParser(IdParser.class);
    }

    public <T extends Parser<?>> T getParser(Class<T> clazz) throws ParserNotSupportedException {
        T parser = (T) this.parsers.get(clazz);
        if (parser == null) {
            throw new ParserNotSupportedException(clazz.getSimpleName() + " is not supported");
        }

        return parser;
    }

    public Set<Class<? extends Parser>> getParserClasses() {
        return this.parsers.keySet();
    }

    public Collection<Parser<?>> getParsers() {
        return this.parsers.values();
    }

    public TextParser getTextParser() throws ParserNotSupportedException {
        return this.getParser(TextParser.class);
    }

    public void register(ParserContainer container) {
        Collection<Parser<?>> maps = container.getParsers();
        this.register(maps.toArray(new Parser<?>[maps.size()]));
    }

    public void register(Parser<?>... parsers) {
        for (Parser<?> parser : parsers) {
            if (!this.containsParser(parser)) {
                this.parsers.put(parser.getClass(), parser);
            }
        }
    }
}
