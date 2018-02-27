package pl.themolka.arcade.capture.wool;

import org.bukkit.DyeColor;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.capture.CapturableFactory;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.xml.XMLDyeColor;
import pl.themolka.arcade.xml.XMLParser;

public class WoolFactory implements CapturableFactory<Wool> {
    @Override
    public Wool newCapturable(CaptureGame game, Participator owner, String id, String name, Element xml) throws JDOMException {
        return this.parseWoolXml(game, xml, new Wool(game, owner, id));
    }

    public Wool parseWoolXml(CaptureGame game, Element xml, Wool wool) {
        DyeColor color = XMLDyeColor.parse(xml.getAttributeValue("color"));
        if (color == null) {
            return null;
        }

        Element monumentElement = xml.getChild("monument");
        Region monument = monumentElement != null ? XMLRegion.parseUnion(game.getGame(), monumentElement) : null;
        if (monument == null) {
            return null;
        }

        // setup
        wool.setColor(color);
        wool.setCraftable(XMLParser.parseBoolean(xml.getAttributeValue("craftable"), false));
        wool.setMonument(monument);
        return wool;
    }
}
