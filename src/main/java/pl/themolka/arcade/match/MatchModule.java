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

package pl.themolka.arcade.match;

import pl.themolka.arcade.command.CommandContext;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.CommandInfo;
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

import java.util.List;

@ModuleInfo(id = "Match")
public class MatchModule extends Module<MatchGame> {
    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(MatchGameParser.class);
    }

    @CommandInfo(name = {"begin", "start"},
            description = "Begin the match",
            flags = {"f", "force"},
            usage = "[-force] [seconds]",
            permission = "arcade.command.begin")
    public void begin(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        MatchGame game = this.getGameModule();
        game.handleBeginCommand(sender, context.getParamInt(0, -1), context.hasFlag("f") || context.hasFlag("force"));
    }

    @CommandInfo(name = {"end", "finish"},
            description = "End current match",
            flags = {"a", "auto",
                    "d", "draw"},
            usage = "[?|*|<winner...>]",
            permission = "arcade.command.end",
            completer = "endCompleter")
    public void end(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        boolean paramAuto = context.hasFlag("a") || context.hasFlag("auto");
        boolean paramDraw = context.hasFlag("d") || context.hasFlag("draw");

        String args = context.getParams(0);
        if (args != null) {
            if (args.equals("?")) {
                paramAuto = true;
            } else if (args.equals("*")) {
                paramDraw = true;
            }
        }

        MatchGame game = this.getGameModule();
        game.handleEndCommand(sender, paramAuto, args, paramDraw);
    }

    public List<String> endCompleter(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        MatchGame game = this.getGameModule();
        return game.handleEndCompleter(sender, context);
    }

    @CommandInfo(name = {"matchinfo", "match"},
            description = "Describe the match",
            permission = "arcade.command.gameinfo") // inherit from /gameinfo
    public void matchInfo(Sender sender, CommandContext context) {
        CommandUtils.sendTitleMessage(sender, "Match", "#" + this.getPlugin().getGames().getCurrentGame().getGameId());
        this.getPlugin().getEventBus().publish(new GameCommands.GameCommandEvent(this.getPlugin(), sender, context));
    }
}
