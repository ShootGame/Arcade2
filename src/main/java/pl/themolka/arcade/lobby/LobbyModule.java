package pl.themolka.arcade.lobby;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;

@ModuleInfo(id = "Lobby")
public class LobbyModule extends Module<LobbyGame> {
    @Override
    public LobbyGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new LobbyGame(new LobbyGame.Config() {
            public boolean enabled() { return true; }
        });
    }

    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) {
        return context.of(LobbyGameParser.class);
    }
}
