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

package pl.themolka.arcade.respawn;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.time.Time;

@Produces(AutoRespawnGame.Config.class)
public class AutoRespawnGameParser extends GameModuleParser<AutoRespawnGame, AutoRespawnGame.Config>
                                   implements InstallableParser {
    private Parser<Ref> filterParser;
    private Parser<Time> cooldownParser;

    public AutoRespawnGameParser() {
        super(AutoRespawnGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("auto-respawn", "autorespawn");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.filterParser = context.type(Ref.class);
        this.cooldownParser = context.type(Time.class);
    }

    @Override
    protected Result<AutoRespawnGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        Ref<Filter.Config<?>> filter = this.filterParser.parse(node.property("filter")).orDefault(Ref.empty());
        Time cooldown = this.cooldownParser.parse(node.property("cooldown", "after")).orDefault(AutoRespawnGame.Config.DEFAULT_COOLDOWN);

        return Result.fine(node, name, value, new AutoRespawnGame.Config() {
            public Ref<Filter.Config<?>> filter() { return filter; }
            public Ref<Time> cooldown() { return Ref.ofProvided(cooldown); }
        });
    }
}
