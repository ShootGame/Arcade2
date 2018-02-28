package pl.themolka.arcade.development;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.environment.Environment;
import pl.themolka.arcade.environment.EnvironmentType;

public class Development extends Environment {
    public Development(Node options, EnvironmentType type) {
        super(options, type);
    }

    @Override
    public void onEnable() {
        this.getPlugin().registerCommandObject(new DevelopmentCommands(this));
    }
}
