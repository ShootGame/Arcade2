package pl.themolka.arcade.respawn;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.time.Time;

@ModuleInfo(id = "Auto-Respawn")
public class AutoRespawnModule extends Module<AutoRespawnGame> {
    public static final Time DEFAULT_COOLDOWN = Time.SECOND;

    @Override
    public AutoRespawnGame buildGameModule(Element xml,
                                           Game game) throws JDOMException {
        return new AutoRespawnGame(DEFAULT_COOLDOWN);
    }
}
