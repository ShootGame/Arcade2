package pl.themolka.arcade.service;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * We are not supporting weather changed dynamically by the server itself.
 * Modules are responsible for what and when should be handled in the game
 * world.
 */
@ServiceId("Weather")
public class WeatherService extends Service {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onThunderChange(ThunderChangeEvent event) {
        event.setCancelled(true);
    }
}
