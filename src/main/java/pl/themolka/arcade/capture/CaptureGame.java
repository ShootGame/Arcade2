package pl.themolka.arcade.capture;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.capture.wool.WoolCapturable;
import pl.themolka.arcade.capture.wool.WoolCapturableFactory;
import pl.themolka.arcade.capture.wool.WoolCapturableListeners;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchWinner;

import java.util.Collection;
import java.util.HashMap;
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

        this.registerWoolListeners();
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
        this.addFactory("wool", new WoolCapturableFactory());
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
                if (id == null || id.isEmpty()) {
                    continue;
                }

                // owner
                String ownerId = xml.getAttributeValue("owner");
                GoalHolder owner = null;
                if (ownerId != null && !ownerId.isEmpty()) {
                    owner = this.getMatch().findWinnerById(ownerId);
                }

                // name
                String name = xml.getAttributeValue("name");
                if (name == null || name.isEmpty()) {
                    continue;
                }

                // object
                Capturable capturable = factory.newCapturable(this, id.trim(), owner, xml);
                if (capturable == null) {
                    continue;
                }

                capturable.setName(name.trim());
                this.addCapturable(capturable);

                // register
                for (MatchWinner winner : this.getMatch().getWinnerList()) {
                    if (capturable.isCompletableBy(winner)) {
                        winner.addGoal(capturable);
                    }
                }

                this.registerListenerObject(capturable); // register listeners
            } catch (JDOMException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void registerWoolListeners() {
        // Register wool listeners ONLY when there are any wool goals.
        Multimap<GoalHolder, WoolCapturable> wools = ArrayListMultimap.create();
        for (Map.Entry<GoalHolder, Capturable> entry : this.capturablesByOwner.entries()) {
            Capturable capturable = entry.getValue();
            if (capturable instanceof WoolCapturable) {
                wools.put(entry.getKey(), (WoolCapturable) capturable);
            }
        }

        if (!wools.isEmpty()) {
            this.registerListenerObject(new WoolCapturableListeners(this, wools));
        }
    }
}
