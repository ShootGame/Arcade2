package pl.themolka.arcade.map;

import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.ArrayList;
import java.util.List;

public class ModulesInfo implements Cloneable {
    private final List<IGameModuleConfig<?>> configs = new ArrayList<>();

    @Override
    public ModulesInfo clone() {
        try {
            return (ModulesInfo) super.clone();
        } catch (CloneNotSupportedException clone) {
            throw new Error(clone);
        }
    }

    public List<IGameModuleConfig<?>> getConfigs() {
        return new ArrayList<>(this.configs);
    }

    public void setConfigs(List<IGameModuleConfig<?>> configs) {
        this.configs.clear();

        if (configs != null) {
            this.configs.addAll(configs);
        }
    }
}
