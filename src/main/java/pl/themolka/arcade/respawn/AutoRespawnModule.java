package pl.themolka.arcade.respawn;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

@ModuleInfo(id = "Auto-Respawn")
public class AutoRespawnModule extends Module<AutoRespawnGame> {
    @Override
    public AutoRespawnGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new AutoRespawnGame();
    }
}
