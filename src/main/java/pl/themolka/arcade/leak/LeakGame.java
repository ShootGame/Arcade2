package pl.themolka.arcade.leak;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Location;
import org.bukkit.material.MaterialData;
import org.jdom2.Element;
import pl.themolka.arcade.event.BlockTransformEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchWinner;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeakGame extends GameModule {
    private final Map<String, Leakable> leakablesById = new HashMap<>();
    private final Map<Liquid.Type, Leakable> leakablesByLiquid = new HashMap<>();
    private final Map<MatchWinner, Leakable> leakablesByOwner = new HashMap<>();

    private Match match;

    @Override
    public void onEnable() {
        this.match = ((MatchGame) this.getGame().getModule(MatchModule.class)).getMatch();

        for (Element leakableElement : this.getSettings().getChildren("leakable")) {
            String paramId = leakableElement.getAttributeValue("id");
            String paramName = leakableElement.getAttributeValue("name");
            String paramOwner = leakableElement.getAttributeValue("owner");
            String paramLiquid = leakableElement.getAttributeValue("liquid");
            String paramDetector = leakableElement.getAttributeValue("detector-level");

            // region
            Region region = this.parseRegion(this.getSettings().getChild("region"));
            if (region == null) {
                continue;
            }

            // id
            String id = paramId;
            if (paramId == null || paramId.isEmpty()) {
                continue;
            }

            // name
            String name = Leakable.DEFAULT_GOAL_NAME;
            if (paramName != null && !paramName.isEmpty()) {
                name = XMLParser.parseMessage(paramName);
            }

            // owner
            MatchWinner owner;
            if (paramOwner == null || paramOwner.isEmpty()) {
                continue;
            } else {
                owner = this.getMatch().findWinner(paramOwner);
                if (owner == null) {
                    continue;
                }
            }

            // liquid
            Liquid.Type liquid = Liquid.DEFAULT_TYPE;
            if (paramLiquid != null && !paramLiquid.isEmpty()) {
                Liquid.Type type = Liquid.Type.valueOf(XMLParser.parseEnumValue(paramLiquid));
                if (type != null) {
                    liquid = type;
                }
            }

            // detector
            int detector = Leakable.DEFAULT_DETECTOR_LEVEL;
            if (paramDetector != null && !paramDetector.isEmpty()) {
                try {
                    detector = Integer.parseInt(paramDetector);
                } catch (NumberFormatException ignored) {
                }
            }

            // object
            Leakable leakable = new Leakable(this, owner, id);
            leakable.setName(name);
            leakable.build(liquid, region, detector);

            this.addLeakable(leakable);
        }
    }

    public void addLeakable(Leakable leakable) {
        this.leakablesById.put(leakable.getId(), leakable);
        this.leakablesByLiquid.put(leakable.getLiquid().getType(), leakable);
        this.leakablesByOwner.put(leakable.getOwner(), leakable);
    }

    public Leakable findLeakableByDetector(Location location) {
        for (Leakable leakable : this.getLeakables()) {
            if (leakable.getDetector().contains(location)) {
                return leakable;
            }
        }

        return null;
    }

    public List<Leakable> findLeakables(Location location) {
        List<Leakable> leakables = new ArrayList<>();
        for (Leakable leakable : this.getLeakables()) {
            if (leakable.getRegion().contains(location)) {
                leakables.add(leakable);
            }
        }

        return leakables;
    }

    public Leakable getLeakable(String id) {
        return this.leakablesById.get(id);
    }

    public Leakable getLeakable(Liquid.Type liquid) {
        return this.leakablesByLiquid.get(liquid);
    }

    public Leakable getLeakable(MatchWinner owner) {
        return this.leakablesByOwner.get(owner);
    }

    public Collection<Leakable> getLeakables() {
        return this.leakablesById.values();
    }

    public Match getMatch() {
        return this.match;
    }

    public void removeLeakable(Leakable leakable) {
        this.leakablesById.remove(leakable.getId());
        this.leakablesByLiquid.remove(leakable.getLiquid().getType());
        this.leakablesByOwner.remove(leakable.getOwner());
    }

    @Handler(priority = Priority.NORMAL)
    public void detectBreak(BlockTransformEvent event) {
        List<Leakable> leakables = this.findLeakables(event.getBlock().getLocation());
        for (Leakable leakable : leakables) {
            if (!event.hasPlayer()) {
                event.setCanceled(true);
                continue;
            }

            GamePlayer player = event.getGamePlayer();
            if (player == null) {
                event.setCanceled(true);
                continue;
            }

            MatchWinner winner = this.getMatch().findWinnerByPlayer(player);
            if (leakable.getOwner().equals(winner)) {
                event.setCanceled(true);
                player.sendError("You may not damage your own " + leakable.getName() + ".");
                continue;
            }

            if (!leakable.breakPiece(winner, player)) {
                event.setCanceled(true);
            }
        }
    }

    @Handler(priority = Priority.LOWEST)
    public void detectLeak(BlockTransformEvent event) {
        Leakable leakable = this.findLeakableByDetector(event.getBlock().getLocation());
        if (leakable == null) {
            return;
        }

        MaterialData newState = event.getNewState();
        if (!leakable.isCompleted() && leakable.getLiquid().getType().accepts(newState.getItemType())) {
            // leakable has leaked
            leakable.leak();
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void detectLiquid(BlockTransformEvent event) {
        for (Leakable leakable : this.getLeakables()) {
            if (leakable.getLiquid().getSnapshot().contains(event.getBlock())) {
                event.setCanceled(true);

                if (event.hasPlayer()) {
                    event.getPlayer().sendError("You may not build inside " + leakable.getName() + ".");
                }
                return;
            }
        }
    }

    private Region parseRegion(Element xml) {
        if (xml != null && !xml.getChildren().isEmpty()) {
            return XMLRegion.parse(this.getGame().getMap(), xml.getChildren().get(0));
        }

        return null;
    }
}
