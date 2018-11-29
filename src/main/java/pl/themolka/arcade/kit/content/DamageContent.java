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

package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class DamageContent implements KitContent<Double> {
    public static final double MIN_VALUE = 0D;

    public static boolean testValue(double value) {
        return value > MIN_VALUE;
    }

    private final double result;

    protected DamageContent(Config config) {
        this.result = config.result().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player) && !player.isDead();
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().damage(this.result);
    }

    @Override
    public Double getResult() {
        return this.result;
    }

    @NestedParserName("damage")
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<Double> damageParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.damageParser = context.type(Double.class);
        }

        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            double damage = this.damageParser.parseWithDefinition(node, name, value).orFail();
            if (damage <= MIN_VALUE) {
                throw this.fail(node, name, value, "Damage must be positive (greater than 0)");
            }

            return Result.fine(node, name, value, new Config() {
                public Ref<Double> result() { return Ref.ofProvided(damage); }
            });
        }
    }

    public interface Config extends KitContent.Config<DamageContent, Double> {
        @Override
        default DamageContent create(Game game, Library library) {
            return new DamageContent(this);
        }
    }
}
