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

import org.bukkit.attribute.Attribute;
import pl.themolka.arcade.attribute.AttributeKey;
import pl.themolka.arcade.attribute.BukkitAttributeKey;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class MaxHealthContent implements RemovableKitContent<Double> {
    public static final AttributeKey MAX_HEALTH = new BukkitAttributeKey(Attribute.GENERIC_MAX_HEALTH);

    public static final double MIN_VALUE = 0D;

    public static boolean testValue(double value) {
        return value > MIN_VALUE;
    }

    private final double result;

    protected MaxHealthContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Double value) {
        if (value != null) {
            player.getAttribute(MAX_HEALTH).setValue(value);
        } else {
            player.getAttribute(MAX_HEALTH).resetValue();
        }
    }

    @Override
    public Double defaultValue() {
        return null;
    }

    @Override
    public Double getResult() {
        return this.result;
    }

    @NestedParserName({"max-health", "maxhealth"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Double> maxHealthParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.maxHealthParser = library.type(Double.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            if (this.reset(context, node)) {
                return Result.fine(node, name, value, new Config() {
                    public Ref<Double> result() { return Ref.empty(); }
                });
            }

            double maxHealth = this.maxHealthParser.parseWithDefinition(context, node, name, value).orFail();
            if (maxHealth <= MIN_VALUE) {
                throw this.fail(node, name, value, "Max health must be positive (greater than 0)");
            }

            return Result.fine(node, name, value, new Config() {
                public Ref<Double> result() { return Ref.ofProvided(maxHealth); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<MaxHealthContent, Double> {
        double DEFAULT_MAX_HEALTH = 20D;

        @Override
        default MaxHealthContent create(Game game, Library library) {
            return new MaxHealthContent(this);
        }
    }
}
