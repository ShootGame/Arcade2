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

import pl.themolka.arcade.attribute.Attribute;
import pl.themolka.arcade.attribute.BoundedModifier;
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

public class ModifierContent implements KitContent<BoundedModifier> {
    private final BoundedModifier result;

    protected ModifierContent(Config config) {
        this.result = config.result().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void apply(GamePlayer player) {
        Attribute attribute = player.getAttribute(this.result.getKey());
        if (attribute != null) {
            attribute.addModifier(this.result.getModifier());
        }
    }

    @Override
    public BoundedModifier getResult() {
        return this.result;
    }

    @NestedParserName({"modifier", "attribute-modifier", "attributemodifier", "attribute"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<BoundedModifier> modifierParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.modifierParser = library.type(BoundedModifier.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            BoundedModifier modifier = this.modifierParser.parseWithDefinition(context, node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<BoundedModifier> result() { return Ref.ofProvided(modifier); }
            });
        }
    }

    public interface Config extends KitContent.Config<ModifierContent, BoundedModifier> {
        @Override
        default ModifierContent create(Game game, Library library) {
            return new ModifierContent(this);
        }
    }
}
