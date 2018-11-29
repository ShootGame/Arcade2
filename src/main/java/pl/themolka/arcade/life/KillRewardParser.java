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

package pl.themolka.arcade.life;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(KillReward.Config.class)
public class KillRewardParser extends ConfigParser<KillReward.Config>
                              implements InstallableParser {
    private Parser<Ref> filterParser;
    private Parser<Ref> kitParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("kill reward");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.filterParser = context.type(Ref.class);
        this.kitParser = context.type(Ref.class);
    }

    @Override
    protected Result<KillReward.Config> parsePrimitive(Node node, String name, String value) throws ParserException {
        String id = this.parseOptionalId(node);
        Ref<Filter.Config<?>> filter = this.filterParser.parse(node.property("filter")).orDefault(Ref.empty());
        Ref<Kit.Config> kit = this.kitParser.parseWithDefinition(node, name, value).orFail();

        return Result.fine(node, name, value, new KillReward.Config() {
            public String id() { return id; }
            public Ref<Filter.Config<?>> filter() { return filter; }
            public Ref<Kit.Config> kit() { return kit; }
        });
    }
}
