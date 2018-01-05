package pl.themolka.arcade.leak;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.leak.core.CoreFactory;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.XMLRegion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LeakGame extends GameModule {
    private final Map<String, Leakable> leakablesById = new HashMap<>();
    private final Multimap<GoalHolder, Leakable> leakablesByOwner = ArrayListMultimap.create();

    private final Map<String, LeakableFactory> factoryMap = new HashMap<>();

    private Match match;

    @Override
    public void onEnable() {
        this.match = ((MatchGame) this.getGame().getModule(MatchModule.class)).getMatch();

        this.installDefaultFactories();
        this.parseMapXml();
    }

    public void addFactory(String name, LeakableFactory factory) {
        this.factoryMap.put(name.toLowerCase(), factory);
    }

    public void addLeakable(Leakable leakable) {
        this.leakablesById.put(leakable.getId(), leakable);

        if (leakable.hasOwner()) {
            this.leakablesByOwner.put(leakable.getOwner(), leakable);
        }
    }

    public LeakableFactory getFactory(String name) {
        return this.factoryMap.get(name.toLowerCase());
    }

    public Leakable getLeakable(String id) {
        return this.leakablesById.get(id);
    }

    public Collection<Leakable> getLeakables() {
        return this.leakablesById.values();
    }

    public Collection<Leakable> getLeakables(GoalHolder owner) {
        return this.leakablesByOwner.get(owner);
    }

    public Match getMatch() {
        return this.match;
    }

    public void removeLeakable(Leakable leakable) {
        this.leakablesById.remove(leakable.getId());

        if (leakable.hasOwner()) {
            this.leakablesByOwner.remove(leakable.getOwner(), leakable);
        }
    }

    private void installDefaultFactories() {
        this.addFactory("core", new CoreFactory());
    }

    private void parseMapXml() {
        for (Element xml : this.getSettings().getChildren()) {
            try {
                // factory
                LeakableFactory factory = this.getFactory(xml.getName());
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
                if (name == null || name.trim().isEmpty()) {
                    continue;
                }

                // object
                Leakable leakable = factory.newLeakable(this, owner, id.trim(), name.trim(), xml);
                if (leakable == null) {
                    continue;
                }

                leakable.setName(name.trim());
                this.addLeakable(leakable);

                // register
                if (leakable.registerGoal()) {
                    for (GoalHolder completableBy : this.match.getWinnerList()) {
                        if (leakable.isCompletableBy(completableBy)) {
                            completableBy.addGoal(leakable);
                        }
                    }
                }

                this.registerListenerObject(leakable); // register listeners
            } catch (JDOMException ex) {
                ex.printStackTrace();
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
