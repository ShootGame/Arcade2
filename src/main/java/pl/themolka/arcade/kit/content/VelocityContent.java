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

import org.bukkit.util.Vector;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class VelocityContent implements RemovableKitContent<Vector> {
    private final Vector result;

    protected VelocityContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Vector value) {
        player.getBukkit().setVelocity(value);
    }

    @Override
    public Vector defaultValue() {
        return Config.DEFAULT_VELOCITY;
    }

    @Override
    public Vector getResult() {
        return this.result;
    }

    @NestedParserName({"velocity", "push", "pull", "jump"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config> {
        private Parser<Vector> velocityParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.velocityParser = library.type(Vector.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            if (this.reset(context, node)) {
                return Result.fine(node, name, value, new Config() {
                    public Ref<Vector> result() { return Ref.empty(); }
                });
            }

            Vector velocity = this.velocityParser.parseWithDefinition(context, node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<Vector> result() { return Ref.ofProvided(velocity); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<VelocityContent, Vector> {
        Vector DEFAULT_VELOCITY = new Vector(0, 0, 0);

        @Override
        default VelocityContent create(Game game, Library library) {
            return new VelocityContent(this);
        }
    }
}
