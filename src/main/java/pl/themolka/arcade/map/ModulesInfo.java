package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.ArrayList;
import java.util.List;

public class ModulesInfo implements Cloneable {
    /**
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
