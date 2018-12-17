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

package pl.themolka.arcade.mob;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(MobSpawnRule.Config.class)
public class MobSpawnRuleParser extends ConfigParser<MobSpawnRule.Config>
                                implements InstallableParser {
    private Parser<Ref> filterParser;
    private Parser<Boolean> allowParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("mob spawn rule");
    }

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.filterParser = library.type(Ref.class);
        this.allowParser = library.type(Boolean.class);
    }

    @Override
    protected Result<MobSpawnRule.Config> parsePrimitive(Context context, Node node, String name, String value) throws ParserException {
        String id = this.parseOptionalId(context, node);
        Ref<Filter.Config<?>> filter = this.filterParser.parse(context, node.property("filter")).orFail();
        boolean allow = this.allowParser.parseWithDefinition(context, node, name, value).orFail();

        return Result.fine(node, name, value, new MobSpawnRule.Config() {
            public String id() { return id; }
            public Ref<Filter.Config<?>> filter() { return filter; }
            public Ref<Boolean> cancel() { return Ref.ofProvided(!allow); }
        });
    }
}
