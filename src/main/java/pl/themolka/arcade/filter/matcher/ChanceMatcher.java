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

package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.util.Percentage;

public class ChanceMatcher extends ConfigurableMatcher<Percentage> {
    protected ChanceMatcher(Config config) {
        super(config.value().get().trim());
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof Percentage) {
            return this.matches((Percentage) object);
        } else if (object instanceof Double) {
            return this.matches((Double) object);
        }

        return this.matches(Percentage.random());
    }

    @Override
    public boolean matches(Percentage percentage) {
        return percentage != null && this.getValue().getValue() >= percentage.getValue();
    }

    public boolean matches(Double number) {
        return number != null && this.matches(Percentage.finite(number.doubleValue()));
    }

    @NestedParserName({"chance", "random"})
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config>
                                      implements InstallableParser {
        private Parser<Percentage> chanceParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.chanceParser = context.type(Percentage.class);
        }

        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            Percentage chance = this.chanceParser.parseWithDefinition(node, name, value).orFail();
            if (!chance.isNormalized()) {
                throw this.fail(node, name, value, "Chance must be normalized");
            }

            return Result.fine(node, name, value, new Config() {
                public Ref<Percentage> value() { return Ref.ofProvided(chance); }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<ChanceMatcher, Percentage> {
        @Override
        default ChanceMatcher create(Game game, Library library) {
            return new ChanceMatcher(this);
        }
    }
}
