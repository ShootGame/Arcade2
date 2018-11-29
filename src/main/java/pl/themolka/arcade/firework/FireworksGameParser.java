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

package pl.themolka.arcade.firework;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

@Produces(FireworksGame.Config.class)
public class FireworksGameParser extends GameModuleParser<FireworksGame, FireworksGame.Config>
                                 implements InstallableParser {
    private Parser<Boolean> enabledParser;

    public FireworksGameParser() {
        super(FireworksGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("fireworks");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.enabledParser = context.type(Boolean.class);
    }

    @Override
    protected Result<FireworksGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        boolean onCoreLeak = this.enabledParser.parse(node.property("core-leak")).orDefault(true);
        boolean onPointCapture = this.enabledParser.parse(node.property("point-capture")).orDefault(true);
        boolean onWoolPlace = this.enabledParser.parse(node.property("wool-place")).orDefault(true);

        if (!onCoreLeak && !onPointCapture && !onWoolPlace) {
            return Result.empty(node, name);
        }

        return Result.fine(node, name, value, new FireworksGame.Config() {
            public Ref<Boolean> onCoreLeak() { return Ref.ofProvided(onCoreLeak); }
            public Ref<Boolean> onPointCapture() { return Ref.ofProvided(onPointCapture); }
            public Ref<Boolean> onWoolPlace() { return Ref.ofProvided(onWoolPlace); }
        });
    }
}
