package pl.themolka.arcade.destroy;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Participator;

public interface DestroyableFactory<E extends Destroyable> {
    E newDestroyable(DestroyGame game, Participator owner, String id, String name, Element xml) throws JDOMException;
}
