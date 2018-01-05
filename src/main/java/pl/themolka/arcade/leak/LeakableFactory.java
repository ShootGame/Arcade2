package pl.themolka.arcade.leak;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.goal.GoalHolder;

public interface LeakableFactory<E extends Leakable> {
    E newLeakable(LeakGame game, GoalHolder owner, String id, String name, Element xml) throws JDOMException;
}
