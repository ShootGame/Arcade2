package pl.themolka.arcade.kit;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.util.Applicable;

import java.util.ArrayList;
import java.util.List;

public class Kit implements Applicable<GamePlayer> {
    private final ArcadePlugin plugin;

    private final List<KitContent<?>> content = new ArrayList<>();
    private final String id;

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
                content.apply(player);
            }
        }
    }

    public boolean addContent(KitContent<?> content) {
        return this.content.add(content);
    }

    public List<KitContent<?>> getContent() {
        return this.content;
    }

    public String getId() {
        return this.id;
    }

    public boolean removeContent(KitContent<?> content) {
        return this.content.remove(content);
    }
}
