package pl.themolka.arcade.capture.point;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.capture.CapturableFactory;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.RegionFieldStrategy;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.score.Score;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.xml.XMLParser;

public class PointFactory implements CapturableFactory<Point> {
    @Override
    public Point newCapturable(CaptureGame game, Participator owner, String id, String name, Element xml) throws JDOMException {
        return this.parsePointXml(game, xml, new Point(game, owner, id));
    }

    public Point parsePointXml(CaptureGame game, Element xml, Point point) {
        // capture region
        Element captureElement = xml.getChild("capture");
        if (captureElement == null) {
            return null;
        }

        PointCapture capture = this.parsePointCapture(game, captureElement, new PointCapture(game, point));
        if (capture == null) {
            return null;
        }

        // state region
        Element stateElement = xml.getChild("state");
        Region stateRegion = stateElement != null ? XMLRegion.parseUnion(game.getGame(), stateElement) : null;
        if (stateRegion == null) {
            stateRegion = capture.getRegion(); // Set state region to the capture region (if it is not set).
        }

        // setup
        point.setCapture(capture);
        point.setCaptureTime(Time.parseTime(xml.getAttributeValue("capture-time"), Point.DEFAULT_CAPTURE_TIME));
        point.setCapturingCapturedEnabled(XMLParser.parseBoolean(xml.getAttributeValue("capturing-captured"), false));
        point.setDominatorStrategy(this.findDominatorStrategy(xml, Point.DEFAULT_DOMINATOR_STRATEGY));
        point.setDominateFilter(this.findFilter(game.getGame(), xml.getAttributeValue("dominate-filter"),
                                                Point.DEFAULT_DOMINATE_FILTER));
        point.setLoseTime(Time.parseTime(xml.getAttributeValue("lose-time"), Point.DEFAULT_LOSE_TIME));
        point.setNeutralColor(Color.parse(xml.getAttributeValue("color"), Point.DEFAULT_NEUTRAL_COLOR));
        point.setObjective(XMLParser.parseBoolean(xml.getAttributeValue("objective"), false));
        point.setPermanent(XMLParser.parseBoolean(xml.getAttributeValue("permanent"), false));
        point.setPointReward(XMLParser.parseDouble(xml.getAttributeValue("point-reward"), Score.ZERO));
        point.setStateRegion(stateRegion);
        return point;
    }

    public PointCapture parsePointCapture(CaptureGame game, Element xml, PointCapture capture) {
        Region region = XMLRegion.parseUnion(game.getGame(), xml);
        if (region == null) {
            return null;
        }

        capture.setFieldStrategy(this.findRegionFieldStrategy(xml, PointCapture.DEFAULT_FIELD_STRATEGY));
        capture.setFilter(this.findFilter(game.getGame(), xml.getAttributeValue("filter"), PointCapture.DEFAULT_FILTER));
        capture.setRegion(region);
        return capture;
    }

    private DominatorStrategy findDominatorStrategy(Element xml, DominatorStrategy def) {
        return xml != null ? DominatorStrategyValues.of(xml.getAttributeValue("dominator"), def) : def;
    }

    private Filter findFilter(Game game, String id, Filter def) {
        if (id != null && !id.trim().isEmpty()) {
            FiltersGame filters = (FiltersGame) game.getModule(FiltersModule.class);
            if (filters != null) {
                return filters.filterOrDefault(id, def);
            }
        }

        return def;
    }

    private RegionFieldStrategy findRegionFieldStrategy(Element xml, RegionFieldStrategy def) {
        return xml != null ? RegionFieldStrategyValues.of(xml.getAttributeValue("field"), def) : def;
    }

    private enum RegionFieldStrategyValues {
        EVERYWHERE(RegionFieldStrategy.EVERYWHERE), // everywhere
        EXACT(RegionFieldStrategy.EXACT), // compare exact (double) locations
        NET(RegionFieldStrategy.NET), // compare block (int) locations
        NET_ROUND(RegionFieldStrategy.NET_ROUND), // compare center block locations
        NOWHERE(RegionFieldStrategy.NOWHERE), // nowhere
        ;

        final RegionFieldStrategy strategy;

        RegionFieldStrategyValues(RegionFieldStrategy strategy) {
            this.strategy = strategy;
        }

        static RegionFieldStrategy of(String value, RegionFieldStrategy def) {
            if (value != null) {
                try {
                    return valueOf(XMLParser.parseEnumValue(value)).strategy;
                } catch (IllegalArgumentException ignored) {
                }
            }

            return def;
        }
    }

    private enum DominatorStrategyValues {
        EVERYBODY(DominatorStrategy.EVERYBODY), // all competitors on the point
        EXCLUSIVE(DominatorStrategy.EXCLUSIVE), // the only competitor on the point
        LEAD(DominatorStrategy.LEAD), // most players on the point
        MAJORITY(DominatorStrategy.MAJORITY), // most players compared to all enemies on the point
        NOBODY(DominatorStrategy.NOBODY), // no competitors
        ;

        final DominatorStrategy strategy;

        DominatorStrategyValues(DominatorStrategy strategy) {
            this.strategy = strategy;
        }

        static DominatorStrategy of(String value, DominatorStrategy def) {
            if (value != null) {
                try {
                    return valueOf(XMLParser.parseEnumValue(value)).strategy;
                } catch (IllegalArgumentException ignored) {
                }
            }

            return def;
        }
    }
}
