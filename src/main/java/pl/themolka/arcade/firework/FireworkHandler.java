package pl.themolka.arcade.firework;

import org.bukkit.event.Listener;

public abstract class FireworkHandler implements Listener {
    private boolean enabled;

    public FireworkHandler(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void onEnable(FireworksGame game) {
    }
}
