package pl.themolka.arcade.capture.flag;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.capture.CapturableFactory;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.goal.GoalHolder;

public class FlagFactory implements CapturableFactory<Flag> {
    @Override
    public Flag newCapturable(CaptureGame game, GoalHolder owner, String id, String name, Element xml) throws JDOMException {
        return this.parseFlagXml(game, name, xml, new Flag(game, owner, id));
    }

    public Flag parseFlagXml(CaptureGame game, String name, Element xml, Flag flag) {
        return flag;
    }
}
