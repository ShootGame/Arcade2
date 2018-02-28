package pl.themolka.arcade.damage;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;

@ModuleInfo(id = "Rage")
@ModuleVersion("1.0")
public class RageModule extends Module<RageGame> {
    public static final double DAMAGE = 10_000;

    @Override
    public RageGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new RageGame(DAMAGE);
    }
}
