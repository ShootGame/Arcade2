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

package pl.themolka.arcade.hunger;

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

@Produces(HungerGame.Config.class)
public class HungerGameParser extends GameModuleParser<HungerGame, HungerGame.Config>
                              implements InstallableParser {
    private Parser<Ref> filterParser;

    public HungerGameParser() {
        super(HungerGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("hunger", "food-level", "foodlevel");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.filterParser = context.type(Ref.class);
    }

    @Override
    protected Result<HungerGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        Ref<Filter.Config<?>> filter = this.filterParser.parse(node.property("filter")).orFail();

        return Result.fine(node, name, value, new HungerGame.Config() {
            public Ref<Filter.Config<?>> filter() { return filter; }
        });
    }
}
