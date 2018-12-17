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

import org.bukkit.potion.PotionEffect;
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

public class EffectContent implements KitContent<PotionEffect>, BaseModeContent {
    private final PotionEffect result;
    private final Mode mode;

    public EffectContent(Config config) {
        this.result = config.result().get();
        this.mode = config.mode();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        if (this.give()) {
            player.getBukkit().addPotionEffect(this.result, true);
        } else if (this.take()) {
            player.getBukkit().removePotionEffect(this.result.getType());
        }
    }

    @Override
    public PotionEffect getResult() {
        return this.result;
    }

    @Override
    public boolean give() {
        return this.mode.equals(Mode.GIVE);
    }

    @Override
    public boolean take() {
        return this.mode.equals(Mode.TAKE);
    }

    @NestedParserName({"effect", "potion-effect", "potioneffect"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<PotionEffect> effectParser;
        private Parser<Mode> modeParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.effectParser = library.type(PotionEffect.class);
            this.modeParser = library.type(Mode.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            PotionEffect effect = this.effectParser.parseWithDefinition(context, node, name, value).orFail();
            BaseModeContent.Mode mode = this.modeParser.parseWithDefinition(context, node, name, value).orDefault(Config.DEFAULT_MODE);

            return Result.fine(node, name, value, new Config() {
                public Ref<PotionEffect> result() { return Ref.ofProvided(effect); }
                public Mode mode() { return mode; }
            });
        }
    }

    public interface Config extends KitContent.Config<EffectContent, PotionEffect>,
                                    BaseModeContent.Config {
        @Override
        default EffectContent create(Game game, Library library) {
            return new EffectContent(this);
        }
    }
}
