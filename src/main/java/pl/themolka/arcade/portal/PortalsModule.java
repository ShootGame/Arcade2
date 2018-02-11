package pl.themolka.arcade.portal;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.kit.KitsModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;
import pl.themolka.arcade.spawn.SpawnsModule;

@ModuleInfo(id = "Portals",
        loadBefore = {
                FiltersModule.class,
                KitsModule.class,
                SpawnsModule.class})
@ModuleVersion("1.0")
public class PortalsModule extends Module<PortalsGame> {
    @Override
    public PortalsGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new PortalsGame();
    }
}
