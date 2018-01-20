package pl.themolka.arcade.capture.flag;

import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.capture.CapturableFactory;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.RegionFieldStrategy;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.xml.XMLParser;

public class FlagFactory implements CapturableFactory<Flag> {
    @Override
    public Flag newCapturable(CaptureGame game, GoalHolder owner, String id, String name, Element xml) throws JDOMException {
        return this.parseFlagXml(game, xml, new Flag(game, owner, id));
    }

    public Flag parseFlagXml(CaptureGame game, Element xml, Flag flag) {
        // capture
        Element captureElement = xml.getChild("capture");
        if (captureElement == null) {
            return null;
        }

        FlagCapture capture = this.parseFlagCapture(game, captureElement, new FlagCapture(game, flag));
        if (capture == null) {
            return null;
        }

        int objective = XMLParser.parseInt(xml.getAttributeValue("objective"), Flag.NOT_OBJECTIVE);

        // spawn
        Element spawnElement = xml.getChild("spawn");
        if (spawnElement == null) {
            return null;
        }

        FlagSpawn spawn = this.parseFlagSpawn(game, captureElement, new FlagSpawn(game, flag));
        if (spawn == null) {
            return null;
        }

        Banner banner = findBannerIn(spawn.getRegion());
        if (banner == null) {
            return null;
        }

        // setup
        flag.setCapture(capture);
        flag.getItem().transferMetaFrom(banner);
        flag.setObjective(objective);
        flag.setSpawn(spawn);
        flag.getSpawn().setDirection(BlockFace.byYaw(banner.getBlock().getLocation().getYaw()));
        return flag;
    }

    public FlagCapture parseFlagCapture(CaptureGame game, Element xml, FlagCapture capture) {
        Region region = XMLRegion.parseUnion(game.getGame().getMap(), xml);
        if (region == null) {
            return null;
        }

        capture.setFieldStrategy(this.findRegionFieldStrategy(xml, FlagCapture.DEFAULT_FIELD_STRATEGY));
        capture.setFilter(this.findFilter(game.getGame(), xml.getAttributeValue("filter"), FlagCapture.DEFAULT_FILTER));
        capture.setRegion(region);
        return capture;
    }

    public FlagSpawn parseFlagSpawn(CaptureGame game, Element xml, FlagSpawn spawn) {
        Region region = XMLRegion.parseUnion(game.getGame().getMap(), xml);
        if (region == null) {
            return null;
        }

        spawn.setFieldStrategy(this.findRegionFieldStrategy(xml, FlagSpawn.DEFAULT_FIELD_STRATEGY));
        spawn.setFilter(this.findFilter(game.getGame(), xml.getAttributeValue("filter"), FlagSpawn.DEFAULT_FILTER));
        spawn.setRegion(region);
        return spawn;
    }

    private Filter findFilter(Game game, String id, Filter def) {
        if (id != null && !id.trim().isEmpty()) {
            GameModule gameModule = game.getModule(FiltersModule.class);

            if (gameModule != null) {
                Filter filter = ((FiltersGame) gameModule).getFilter(id.trim());
                if (filter != null) {
                    return filter;
                }
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

    public static Banner findBannerIn(Region region) {
        for (Block block : region.getBlocks()) {
            if (BannerUtils.isBanner(block)) {
                return (Banner) block.getState();
            }
        }

        return null;
    }
}
