package pl.themolka.arcade.service;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.task.SimpleTaskListener;

@ServiceId("SetNextRestart")
public class SetNextRestartService extends Service {
    @Override
    protected void loadService() {
        this.getPlugin().getTasks().scheduleAsync(new SimpleTaskListener() {
            boolean done = false;

            @Override
            public void onDay(long days) {
                if (!this.done) {
                    ArcadePlugin plugin = getPlugin();
                    plugin.getLogger().info("Server ran now for 24 hours. Will be restarting soon...");
                    plugin.getGames().setNextRestart(true);
                    this.done = true;
                }
            }
        });
    }
}
