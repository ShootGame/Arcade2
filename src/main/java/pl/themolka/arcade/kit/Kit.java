package pl.themolka.arcade.kit;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.content.KitContent;
import pl.themolka.arcade.util.Applicable;
import pl.themolka.arcade.util.StringId;

import java.util.ArrayList;
import java.util.List;

public class Kit implements Applicable<GamePlayer>, StringId {
    private final ArcadePlugin plugin;

    private final List<KitContent<?>> content = new ArrayList<>();
    private final String id;
    private final List<String> inherit = new ArrayList<>();

    public Kit(ArcadePlugin plugin, String id) {
        this.plugin = plugin;

        this.id = id;
    }

    @Override
    public void apply(GamePlayer player) {
        KitApplyEvent event = new KitApplyEvent(this.plugin, this, player);
        this.plugin.getEventBus().publish(event);

        if (!event.isCanceled()) {
            for (KitContent<?> content : this.getContent()) {
                if (content.isApplicable(player)) {
                    content.apply(player);
                }
            }
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void addContent(Kit kit) {
        this.addContent(kit.getContent().toArray(new KitContent[kit.getContent().size()]));
    }

    public boolean addContent(KitContent<?> content) {
        return this.content.add(content);
    }

    public void addContent(KitContent<?>... array) {
        for (KitContent<?> content : array) {
            this.addContent(content);
        }
    }

    public boolean addInherit(String inherit) {
        return this.inherit.add(inherit);
    }

    public void clear() {
        this.content.clear();
    }

    public List<KitContent<?>> getContent() {
        return this.content;
    }

    public List<String> getInherit() {
        return this.inherit;
    }

    public boolean removeContent(KitContent<?> content) {
        return this.content.remove(content);
    }
}
