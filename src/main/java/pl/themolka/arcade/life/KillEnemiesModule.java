package pl.themolka.arcade.life;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

@ModuleInfo(id = "Kill-Enemies")
public class KillEnemiesModule extends Module<KillEnemiesGame> {
    @Override
    public KillEnemiesGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new KillEnemiesGame();
    }
}
