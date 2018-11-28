package pl.themolka.arcade.gamerule;

import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

@ModuleInfo(id = "GameRules")
public class GameRulesModule extends Module<GameRulesGame> {
    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(GameRulesGameParser.class);
    }
}
