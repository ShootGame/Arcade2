package pl.themolka.arcade.filter;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

@ModuleInfo(id = "Filters")
public class FiltersModule extends Module<FiltersGame> {
    @Override
    public FiltersGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new FiltersGame();
    }
}
