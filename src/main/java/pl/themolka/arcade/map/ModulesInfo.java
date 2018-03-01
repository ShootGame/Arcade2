package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.ArrayList;
import java.util.List;

public class ModulesInfo implements Cloneable {
    /**
     * This is not the best way to store modules to load. The thing is that they
     * <b>CANNOT</b> be parsed until we have a Game object created. That's
     * because they will parse GameModule objects.
     *
     * TODO: Consider about using "configs" with pre-defined module settings.
     * The configs should ONLY store references to other modules, or features.
     * The configs could then be converted into fine GameModule objects.
     * @deprecated Legacy
     */
    @Deprecated
    private final List<Node> modules = new ArrayList<>();
    private final List<IGameModuleConfig<?>> configs = new ArrayList<>();

    @Override
    public ModulesInfo clone() {
        try {
            ModulesInfo clone = (ModulesInfo) super.clone();
            clone.modules.addAll(new ArrayList<>(this.modules));
            return clone;
        } catch (CloneNotSupportedException clone) {
            throw new Error(clone);
        }
    }

    public List<Node> getModules() {
        return new ArrayList<>(this.modules);
    }

    public List<IGameModuleConfig<?>> getConfigs() {
        return new ArrayList<>(this.configs);
    }

    public void setModules(List<Node> modules) {
        this.modules.clear();

        if (modules != null) {
            this.modules.addAll(modules);
        }
    }

    public void setConfigs(List<IGameModuleConfig<?>> configs) {
        this.configs.clear();

        if (configs != null) {
            this.configs.addAll(configs);
        }
    }
}
