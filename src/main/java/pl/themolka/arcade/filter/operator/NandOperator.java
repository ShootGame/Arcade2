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

package pl.themolka.arcade.filter.operator;

import pl.themolka.arcade.condition.AbstainableResult;
import pl.themolka.arcade.condition.NandCondition;
import pl.themolka.arcade.condition.OptionalResult;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Set;

public class NandOperator extends Operator {
    protected NandOperator(Game game, IGameConfig.Library library, Config config) {
        super(game, library, config);
    }

    @Override
    public AbstainableResult filter(Object... objects) {
        return OptionalResult.valueOf(new NandCondition(this.getBody()).query(objects));
    }

    @NestedParserName("nand")
    @Produces(Config.class)
    public static class OperatorParser extends BaseOperatorParser<Config> {
        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            Set<Filter.Config<?>> body = this.parseBody(context, node, name, value);

            return Result.fine(node, name, value, new Config() {
                public Ref<Set<Filter.Config<?>>> body() { return Ref.ofProvided(body); }
            });
        }
    }

    public interface Config extends Operator.Config<NandOperator> {
        @Override
        default NandOperator create(Game game, Library library) {
            return new NandOperator(game, library, this);
        }
    }
}
