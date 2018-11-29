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

import pl.themolka.arcade.command.CommandContext;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.CommandInfo;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

@ModuleInfo(id = "Channels")
public class ChannelsModule extends Module<ChannelsGame> {
    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(ChannelsGameParser.class);
    }

    @CommandInfo(name = {"global", "g", "!"},
            description = "Send a message to the global chat channel",
            min = 1,
            flags = {"c", "console"},
            usage = "[-console] <message...>",
            permission = "arcade.channel.global")
    public void global(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Channels module is not enabled in this game.");
        }

        boolean console = context.hasFlag("c") || context.hasFlag("console");
        if (console && sender.hasPermission("arcade.channel.global.console")) {
            sender = this.getPlugin().getConsole();
        }

        this.getGameModule().getGlobalChannel().sendChatMessage(sender, context.getParams(0));
    }
}
