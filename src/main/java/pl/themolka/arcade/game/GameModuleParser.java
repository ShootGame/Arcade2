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

package pl.themolka.arcade.game;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Base class for all {@link GameModule} parsers.
 */
public abstract class GameModuleParser<M extends GameModule,
                                       T extends IGameModuleConfig<M>> extends ConfigParser<T>
                                                                       implements InstallableParser {
    private final Class<M> moduleClass;

    public GameModuleParser(Class<M> moduleClass) {
        this.moduleClass = Objects.requireNonNull(moduleClass, "moduleClass cannot be null");
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton(this.getModuleClass().getSimpleName() + " module");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
    }

    public abstract Node define(Node source);

    public Class<M> getModuleClass() {
        return this.moduleClass;
    }
}
