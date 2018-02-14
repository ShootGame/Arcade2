package pl.themolka.arcade.damage;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;

@ModuleInfo(id = "Damage",
        loadBefore = {
                FiltersModule.class})
@ModuleVersion("1.0")
public class DamageModule extends Module<GameModule> {
    @Override
    public GameModule buildGameModule(Element xml, Game game) throws JDOMException {
        return new DamageGame();
    }
}
