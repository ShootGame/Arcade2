package pl.themolka.arcade.region;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

@ModuleInfo(id = "Regions",
        loadBefore = {
                FiltersModule.class})
public class RegionsModule extends Module<RegionsGame> {
    @Override
    public RegionsGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new RegionsGame();
    }
}
