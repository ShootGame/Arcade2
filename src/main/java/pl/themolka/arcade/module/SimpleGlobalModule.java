package pl.themolka.arcade.module;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

public class SimpleGlobalModule extends Module<GameModule> {
    @Override
    public final GameModule buildGameModule(Element xml, Game game) throws JDOMException {
        return super.buildGameModule(xml, game);
    }

    @Override
    public final GameModule createGameModule(Game game, IGameModuleConfig<GameModule> config) {
        return super.createGameModule(game, config);
    }

    @Override
    public final GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return super.getGameModuleParser(context);
    }
}
