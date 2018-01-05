package pl.themolka.arcade.capture.point;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.capture.CapturableFactory;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.time.XMLTime;
import pl.themolka.arcade.xml.XMLParser;

public class PointFactory implements CapturableFactory<Point> {
    @Override
    public Point newCapturable(CaptureGame game, GoalHolder owner, String id, String name, Element xml) throws JDOMException {
        return this.parsePointXml(game, name, xml, new Point(game, owner, id));
    }

    public Point parsePointXml(CaptureGame game, String name, Element xml, Point point) {
        // capture region
        Element captureRegionElement = xml.getChild("capture-region");
        Region captureRegion = captureRegionElement != null ? XMLRegion.parseUnion(game.getGame().getMap(), captureRegionElement) : null;
        if (captureRegion == null) {
            return null;
        }

        // state region
        Element stateRegionElement = xml.getChild("state-region");
        Region stateRegion = stateRegionElement != null ? XMLRegion.parseUnion(game.getGame().getMap(), stateRegionElement) : null;
        if (stateRegion == null) {
            stateRegion = captureRegion; // set state region to the capture region (if it is not set)
        }

        // setup
        point.setCaptureRegion(captureRegion);
        point.setCaptureTime(XMLTime.parse(xml.getAttributeValue("capture-time"), Point.DEFAULT_CAPTURE_TIME));
        point.setCapturingCapturedEnabled(XMLParser.parseBoolean(xml.getAttributeValue("capturing-captured"), true));
        point.setLoseTime(XMLTime.parse(xml.getAttributeValue("lose-time"), Point.DEFAULT_LOSE_TIME));
        point.setPermanent(XMLParser.parseBoolean(xml.getAttributeValue("permanent"), false));
        point.setPointReward(XMLParser.parseInt(xml.getAttributeValue("point-reward"), 0));
        point.setStateRegion(stateRegion);
        return point;
    }
}
