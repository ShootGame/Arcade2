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

public class GiveExperienceContent implements KitContent<Integer> {
    public static boolean testValue(int value) {
        return value != 0;
    }

    private final int result;

    protected GiveExperienceContent(Config config) {
        this.result = config.result().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().giveExp(this.result);
    }

    @Override
    public Integer getResult() {
        return this.result;
    }

    @NestedParserName({"give-experience", "giveexperience", "give-experiences", "giveexperiences",
                       "take-experience", "takeexperience", "take-experiences", "takeexperiences"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<Integer> experienceParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.experienceParser = library.type(Integer.class);
        }

        @Override
        protected Result<Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            int experience = this.experienceParser.parseWithDefinition(context, node, name, value).orFail();
            if (experience <= 0) {
                throw this.fail(node, name, value, "Experience must be positive (greater than 0)");
            }

            if (name.toLowerCase().startsWith("take")) {
                experience = -experience;
            }

            final int finalExperience = experience;
            return Result.fine(node, name, value, new Config() {
                public Ref<Integer> result() { return Ref.ofProvided(finalExperience); }
            });
        }
    }

    public interface Config extends KitContent.Config<GiveExperienceContent, Integer> {
        @Override
        default GiveExperienceContent create(Game game, Library library) {
            return new GiveExperienceContent(this);
        }
    }
}
