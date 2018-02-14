package pl.themolka.arcade.leak;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Participator;

public interface LeakableFactory<E extends Leakable> {
    E newLeakable(LeakGame game, Participator owner, String id, String name, Element xml) throws JDOMException;
}
