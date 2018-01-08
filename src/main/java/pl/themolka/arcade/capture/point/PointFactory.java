package pl.themolka.arcade.capture.point;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.capture.CapturableFactory;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.RegionFinder;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.score.Score;
import pl.themolka.arcade.time.XMLTime;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.xml.XMLParser;

public class PointFactory implements CapturableFactory<Point> {
    @Override
    public Point newCapturable(CaptureGame game, GoalHolder owner, String id, String name, Element xml) throws JDOMException {
        return this.parsePointXml(game, name, xml, new Point(game, owner, id));
    }

    public Point parsePointXml(CaptureGame game, String name, Element xml, Point point) {
        // capture region
        Element captureElement = xml.getChild("capture");
        Region captureRegion = captureElement != null ? XMLRegion.parseUnion(game.getGame().getMap(), captureElement) : null;
        if (captureRegion == null) {
            return null;
        }

        // state region
        Element stateElement = xml.getChild("state");
        Region stateRegion = stateElement != null ? XMLRegion.parseUnion(game.getGame().getMap(), stateElement) : null;
        if (stateRegion == null) {
            stateRegion = captureRegion; // set state region to the capture region (if it is not set)
        }

        // setup
        point.setCaptureFilter(this.findFilter(game.getGame(), captureElement.getAttributeValue("filter")));
        point.setCaptureRegion(captureRegion);
        point.setCaptureRegionFinder(this.findRegionFinder(captureElement, Point.DEFAULT_CAPTURE_REGION_FINDER));
        point.setCaptureTime(XMLTime.parse(captureElement.getAttributeValue("time"), Point.DEFAULT_CAPTURE_TIME));
        point.setCapturingCapturedEnabled(XMLParser.parseBoolean(captureElement.getAttributeValue("capturing-captured"), true));
        point.setDominateFilter(this.findFilter(game.getGame(), xml.getAttributeValue("dominate-filter")));
        point.setLoseTime(XMLTime.parse(xml.getAttributeValue("lose-time"), Point.DEFAULT_LOSE_TIME));
        point.setNeutralColor(Color.parse(xml.getAttributeValue("color"), Point.DEFAULT_NEUTRAL_COLOR));
        point.setObjective(XMLParser.parseBoolean(xml.getAttributeValue("objective"), false));
        point.setPermanent(XMLParser.parseBoolean(xml.getAttributeValue("permanent"), false));
        point.setPointReward(XMLParser.parseDouble(xml.getAttributeValue("point-reward"), Score.ZERO));
        point.setStateRegion(stateRegion);
        return point;
    }

    private Filter findFilter(Game game, String id) {
        if (id != null && !id.trim().isEmpty()) {
            GameModule gameModule = game.getModule(FiltersModule.class);

            if (gameModule != null) {
                Filter filter = ((FiltersGame) gameModule).getFilter(id.trim());
                if (filter != null) {
                    return filter;
                }
            }
        }

        return Filters.undefined();
    }

    private RegionFinder findRegionFinder(Element xml, RegionFinder def) {
        if (xml != null) {
            try {
                String key = XMLParser.parseEnumValue(xml.getAttributeValue("find-mode"));
                return RegionFinderValues.valueOf(key).finder;
            } catch (IllegalArgumentException ignored) {
            }
        }

        return def;
    }

    private enum RegionFinderValues {
        EVERYWHERE(RegionFinder.EVERYWHERE),
        EXACT(RegionFinder.EXACT),
        NET(RegionFinder.NET),
        NET_ROUND(RegionFinder.NET_ROUND),
        NOWHERE(RegionFinder.NOWHERE)
        ;

        final RegionFinder finder;

        RegionFinderValues(RegionFinder finder) {
            this.finder = finder;
        }
    }
}
