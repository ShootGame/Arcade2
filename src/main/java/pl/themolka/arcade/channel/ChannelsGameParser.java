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

package pl.themolka.arcade.channel;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

@Produces(ChannelsGame.Config.class)
public class ChannelsGameParser extends GameModuleParser<ChannelsGame, ChannelsGame.Config> {
    public ChannelsGameParser() {
        super(ChannelsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("channels", "chat-channels", "chatchannels");
    }

    @Override
    protected Result<ChannelsGame.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
        return Result.fine(node, name, value, new ChannelsGame.Config() {});
    }
}
