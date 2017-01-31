package pl.themolka.arcade.lobby;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

@ModuleInfo(id = "lobby")
public class LobbyModule extends Module<LobbyGame> {
    @Override
    public LobbyGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new LobbyGame();
    }
}
