package pl.themolka.arcade.capture;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.capture.flag.FlagFactory;
import pl.themolka.arcade.capture.point.Point;
import pl.themolka.arcade.capture.point.PointBossBarRender;
import pl.themolka.arcade.capture.point.PointFactory;
import pl.themolka.arcade.capture.point.PointRegionRender;
import pl.themolka.arcade.capture.wool.Wool;
import pl.themolka.arcade.capture.wool.WoolFactory;
import pl.themolka.arcade.capture.wool.WoolListeners;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchWinner;
import pl.themolka.arcade.task.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptureGame extends GameModule {
    private final Map<String, Capturable> capturablesById = new HashMap<>();
    private final Multimap<GoalHolder, Capturable> capturablesByOwner = ArrayListMultimap.create();

    private final Map<String, CapturableFactory> factoryMap = new HashMap<>();

    private Match match;

    @Override
    public void onEnable() {
        this.match = ((MatchGame) this.getGame().getModule(MatchModule.class)).getMatch();

        this.installDefaultFactories();
        this.parseMapXml();

        this.enableFlags();
        this.enablePoints();
        this.enableWools();
    }

    public void addCapturable(Capturable capturable) {
        this.capturablesById.put(capturable.getId(), capturable);

        if (capturable.hasOwner()) {
            this.capturablesByOwner.put(capturable.getOwner(), capturable);
        }
    }

    public void addFactory(String name, CapturableFactory factory) {
        this.factoryMap.put(name.toLowerCase(), factory);
    }

    public Capturable getCapturable(String id) {
        return this.capturablesById.get(id);
    }

    public Collection<Capturable> getCapturables() {
        return this.capturablesById.values();
    }

    public Collection<Capturable> getCapturables(GoalHolder owner) {
        return this.capturablesByOwner.get(owner);
    }

    public CapturableFactory getFactory(String name) {
        return this.factoryMap.get(name.toLowerCase());
    }

    public Match getMatch() {
        return this.match;
    }

    public void removeCapturable(Capturable capturable) {
        this.capturablesById.remove(capturable.getId());

        if (capturable.hasOwner()) {
            this.capturablesByOwner.remove(capturable.getOwner(), capturable);
        }
    }

    private void installDefaultFactories() {
        this.addFactory("flag", new FlagFactory());
        this.addFactory("point", new PointFactory());
        this.addFactory("wool", new WoolFactory());
    }

    private void parseMapXml() {
        for (Element xml : this.getSettings().getChildren()) {
            try {
                // factory
                CapturableFactory factory = this.getFactory(xml.getName());
                if (factory == null) {
                    continue;
                }

                // id
                String id = xml.getAttributeValue("id");
                if (id == null || id.trim().isEmpty()) {
                    continue;
                }

                // owner
                String ownerId = xml.getAttributeValue("owner");
                GoalHolder owner = null;
                if (ownerId != null && !ownerId.trim().isEmpty()) {
                    owner = this.getMatch().findWinnerById(ownerId.trim());
                }

                // name
                String name = xml.getAttributeValue("name");
                if (name != null) {
                    name = name.trim();
                }

                // object
                Capturable capturable = factory.newCapturable(this, owner, id.trim(), name, xml);
                if (capturable == null) {
                    continue;
                }

                capturable.setName(name);
                this.addCapturable(capturable);

                // register
                if (capturable.registerGoal()) {
                    for (MatchWinner winner : this.getMatch().getWinnerList()) {
                        if (capturable.isCompletableBy(winner)) {
                            winner.addGoal(capturable);
                        }
                    }
                }

                this.registerListenerObject(capturable); // register listeners
            } catch (JDOMException ex) {
                ex.printStackTrace();
            }
        }
    }

    //
    // Enabling Capturables
    //

    private void enableFlags() {
    }

    private void enablePoints() {
        // Register point classes ONLY when there are any point goals.
        List<Point> points = new ArrayList<>();

        for (Capturable capturable : this.getCapturables()) {
            if (capturable instanceof Point) {
                points.add((Point) capturable);
            }
        }

        if (!points.isEmpty()) {
            this.scheduleSyncTask(new Task(this.getPlugin().getTasks()) {
                @Override
                public void onTick(long ticks) {
                    if (getMatch().isRunning() && ticks % Point.HEARTBEAT_INTERVAL.toTicks() == 0) {
                        for (Point point : points) {
                            point.heartbeat(ticks);
                        }
                    }
                }
            });

            this.registerListenerObject(new PointBossBarRender(this));
            this.registerListenerObject(new PointRegionRender(this));
        }
    }

    private void enableWools() {
        // Register wool classes ONLY when there are any wool goals.
        List<Wool> standaloneWools = new ArrayList<>();
        Multimap<GoalHolder, Wool> wools = ArrayListMultimap.create();

        for (Capturable capturable : this.getCapturables()) {
            if (capturable instanceof Wool) {
                GoalHolder owner = capturable.getOwner();
                if (owner != null) {
                    wools.put(owner, (Wool) capturable);
                } else {
                    standaloneWools.add((Wool) capturable);
                }
            }
        }

        if (!standaloneWools.isEmpty() || !wools.isEmpty()) {
            this.registerListenerObject(new WoolListeners(this, standaloneWools, wools));
        }
    }
}
