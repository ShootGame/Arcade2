package pl.themolka.arcade.capture;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Participator;

public interface CapturableFactory<E extends Capturable> {
    E newCapturable(CaptureGame game, Participator owner, String id, String name, Element xml) throws JDOMException;
}
