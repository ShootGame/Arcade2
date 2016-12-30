package pl.themolka.arcade.devlopment;

import org.jdom2.Element;
import pl.themolka.arcade.environment.Environment;
import pl.themolka.arcade.environment.EnvironmentType;

public class Development extends Environment {
    public Development(Element settings, EnvironmentType type) {
        super(settings, type);
    }

    @Override
    public void onEnable() {
        Element settings = this.getSettings();

        Element eventDebug = settings.getChild("event-debug");
        if (eventDebug != null && eventDebug.getTextNormalize().equalsIgnoreCase("true")) {
            this.getPlugin().getEvents().registerDebugHandler(new SimpleEventDebugHandler(this));
        }
    }
}
