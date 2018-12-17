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
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class WalkSpeedContent implements RemovableKitContent<Float> {
    public static final float MIN_VALUE = -1F;
    public static final float MAX_VALUE = +1F;

    public static boolean testValue(float value) {
        return value >= MIN_VALUE && value <= MAX_VALUE;
    }

    private final float result;

    protected WalkSpeedContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Float value) {
        player.getBukkit().setWalkSpeed(value);
    }

    @Override
    public Float defaultValue() {
        return Config.DEFAULT_SPEED;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    @NestedParserName({"walk-speed", "walkspeed"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Float> speedParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.speedParser = library.type(Float.class);
        }

        @Override
        protected Result<Config> parsePrimitive(Context context, Node node, String name, String value) throws ParserException {
            if (this.reset(context, node)) {
                return Result.fine(node, name, value, new Config() {
                    public Ref<Float> result() { return Ref.empty(); }
                });
            }

            float speed = this.speedParser.parseWithDefinition(context, node, name,  value).orFail();
            if (speed < MIN_VALUE) {
                throw this.fail(node, name, value, "Walk speed is too slow (min " + MIN_VALUE + ")");
            } else if (speed > MAX_VALUE) {
                throw this.fail(node, name, value, "Walk speed is too fast (max " + MAX_VALUE + ")");
            }

            return Result.fine(node, name, value, new Config() {
                public Ref<Float> result() { return Ref.ofProvided(speed); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<WalkSpeedContent, Float> {
        float DEFAULT_SPEED = 0.2F;

        @Override
        default WalkSpeedContent create(Game game, Library library) {
            return new WalkSpeedContent(this);
        }
    }
}
