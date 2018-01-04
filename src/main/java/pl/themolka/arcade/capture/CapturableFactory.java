package pl.themolka.arcade.capture;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.goal.GoalHolder;

public interface CapturableFactory<E extends Capturable> {
    E newCapturable(CaptureGame game, String id, GoalHolder owner, Element xml) throws JDOMException;
}
