package pl.themolka.arcade.damage;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;

@ModuleInfo(id = "Damage",
        dependency = {
                FiltersModule.class})
@ModuleVersion("1.0")
public class DamageModule extends Module<DamageGame> {
    @Override
    public DamageGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new DamageGame();
    }
}