package pl.themolka.arcade.capture.wool;

import org.bukkit.DyeColor;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.capture.CapturableFactory;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.xml.XMLDyeColor;
import pl.themolka.arcade.xml.XMLParser;

public class WoolCapturableFactory implements CapturableFactory<WoolCapturable> {
    @Override
    public WoolCapturable newCapturable(CaptureGame game, GoalHolder owner, String id, String name, Element xml) throws JDOMException {
        return this.parseWoolXml(game, name, xml, new WoolCapturable(game, owner, id));
    }

    public WoolCapturable parseWoolXml(CaptureGame game, String name, Element xml, WoolCapturable wool) {
        String craftable = xml.getAttributeValue("craftable");
        Element monumentElement = xml.getChild("monument");

        DyeColor color = XMLDyeColor.parse(xml.getAttributeValue("color"));
        if (color == null) {
            return null;
        }

        Region monument = monumentElement != null ? XMLRegion.parseUnion(game.getGame().getMap(), monumentElement) : null;
        if (monument == null) {
            return null;
        }

        // setup
        wool.setColor(color);
        wool.setCraftable(craftable != null && XMLParser.parseBoolean(craftable, false));
        wool.setMonument(monument);
        return wool;
    }
}
