package pl.themolka.arcade.damage;

import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

@ModuleInfo(id = "Rage")
public class RageModule extends Module<RageGame> {
    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(RageGameParser.class);
    }
}
