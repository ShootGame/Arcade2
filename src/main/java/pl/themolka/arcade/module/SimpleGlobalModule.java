package pl.themolka.arcade.module;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;

public class SimpleGlobalModule extends Module<Object> {
    @Override
    public final Object buildGameModule(Element xml, Game game) throws JDOMException {
        return super.buildGameModule(xml, game);
    }
}
