package pl.themolka.arcade.destroy;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.MatchWinner;
import pl.themolka.arcade.xml.XMLParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DestroyGame extends GameModule {
    private final Map<String, Destroyable> destroyablesById = new HashMap<>();
    private final Multimap<Participator, Destroyable> destroyablesByOwner = ArrayListMultimap.create();

    private final Map<String, DestroyableFactory> factoryMap = new HashMap<>();

    private Match match;

    @Override
    public void onEnable() {
        this.match = ((MatchGame) this.getGame().getModule(MatchModule.class)).getMatch();

        this.installDefaultFactories();
        this.parseMapXml();
    }

    public void addDestroyable(Destroyable destroyable) {
        this.destroyablesById.put(destroyable.getId(), destroyable);

        if (destroyable.hasOwner()) {
            this.destroyablesByOwner.put(destroyable.getOwner(), destroyable);
        }
    }

    public void addFactory(String name, DestroyableFactory factory) {
        this.factoryMap.put(name.toLowerCase(), factory);
    }

    public Destroyable getDestroyable(String id) {
        return this.destroyablesById.get(id);
    }

    public Collection<Destroyable> getDestroyable() {
        return this.destroyablesById.values();
    }

    public Collection<Destroyable> getDestroyable(Participator owner) {
        return this.destroyablesByOwner.get(owner);
    }

    public DestroyableFactory getFactory(String name) {
        return this.factoryMap.get(name.toLowerCase());
    }

    public Match getMatch() {
        return this.match;
    }

    public void removeDestroyable(Destroyable destroyable) {
        this.destroyablesById.remove(destroyable.getId());

        if (destroyable.hasOwner()) {
            this.destroyablesByOwner.remove(destroyable.getOwner(), destroyable);
        }
    }

    private void installDefaultFactories() {
    }

    private void parseMapXml() {
        for (Element xml : this.getSettings().getChildren()) {
            try {
                // factory
                DestroyableFactory factory = this.getFactory(xml.getName());
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
                Participator owner = null;
                if (ownerId != null && !ownerId.trim().isEmpty()) {
                    owner = this.getMatch().findWinnerById(ownerId.trim());
                }

                // name
                String name = XMLParser.parseMessage(xml.getAttributeValue("name"));

                // object
                Destroyable destroyable = factory.newDestroyable(this, owner, id.trim(), name, xml);
                if (destroyable == null) {
                    continue;
                }

                destroyable.setName(name);
                this.addDestroyable(destroyable);

                // register
                if (destroyable.registerGoal()) {
                    for (MatchWinner winner : this.getMatch().getWinnerList()) {
                        if (destroyable.isCompletableBy(winner)) {
                            winner.addGoal(destroyable);
                        }
                    }
                }

                destroyable.registerEventListeners(this, true); // register listeners
            } catch (JDOMException ex) {
                ex.printStackTrace();
            }
        }
    }

    //
    // Enabling Destroyables
    //
}
