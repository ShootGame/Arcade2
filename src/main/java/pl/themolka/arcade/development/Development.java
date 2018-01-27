package pl.themolka.arcade.development;

import org.jdom2.Element;
import pl.themolka.arcade.environment.Environment;
import pl.themolka.arcade.environment.EnvironmentType;

public class Development extends Environment {
    public Development(Element settings, EnvironmentType type) {
        super(settings, type);
    }

    @Override
    public void onEnable() {
        this.getPlugin().registerCommandObject(new DevelopmentCommands(this));

        Element settings = this.getSettings();
    }
}
