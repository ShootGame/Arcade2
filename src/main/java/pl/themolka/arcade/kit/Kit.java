package pl.themolka.arcade.kit;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.config.Unique;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.kit.content.KitContent;
import pl.themolka.arcade.util.Applicable;
import pl.themolka.arcade.util.StringId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Kit implements Applicable<GamePlayer>, StringId {
    private final ArcadePlugin plugin;

    private final List<KitContent<?>> content = new ArrayList<>();
    private final String id;
    private final Set<String> inherit = new LinkedHashSet<>();

    protected Kit(Game game, IGameConfig.Library library, Config config) {
        this.plugin = game.getPlugin();
        this.id = config.id();

        for (KitContent.Config<?, ?> content : config.contents().get()) {
            this.content.add(library.getOrDefine(game, content));
        }

        this.inherit.addAll(config.inherit().get());
    }

    @Override
    public void apply(GamePlayer player) {
        this.apply(player, false);
    }

    public void apply(GamePlayer player, boolean secret) {
        boolean allow = true;
        if (!secret) {
            KitApplyEvent event = new KitApplyEvent(this.plugin, this, player);
            allow = !this.plugin.getEventBus().postEvent(event).isCanceled();
        }

        if (allow) {
            for (KitContent<?> content : this.content) {
                this.applyContent(content, player);
            }
        }
    }

    protected void applyContent(KitContent<?> content, GamePlayer player) {
        content.applyIfApplicable(player);
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
        return new ArrayList<>(this.content);
    }

    public Set<String> getInherit() {
        return new LinkedHashSet<>(this.inherit);
    }

    public boolean removeContent(KitContent<?> content) {
        return this.content.remove(content);
    }

    public interface Config extends IGameConfig<Kit>, Unique {
        Ref<List<KitContent.Config<?, ?>>> contents();
        default Ref<Set<String>> inherit() { return Ref.ofProvided(Collections.emptySet()); }

        @Override
        default Kit create(Game game, Library library) {
            return new Kit(game, library, this);
        }
    }
}
