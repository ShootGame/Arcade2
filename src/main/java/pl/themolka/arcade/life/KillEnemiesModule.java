package pl.themolka.arcade.life;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

@ModuleInfo(id = "Kill-Enemies",
        dependency = {
                MatchModule.class})
public class KillEnemiesModule extends Module<KillEnemiesGame> {
    @Override
    public KillEnemiesGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new KillEnemiesGame();
    }

    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(KillEnemiesGameParser.class);
    }
}
