package pl.themolka.arcade.capture;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import pl.themolka.arcade.capture.flag.Flag;
import pl.themolka.arcade.capture.flag.FlagPayloadRender;
import pl.themolka.arcade.capture.flag.FlagPhysicalRender;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchGame;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.task.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptureGame extends GameModule {
    private final Map<String, Capturable> capturablesById = new HashMap<>();
    private final Multimap<Participator, Capturable> capturablesByOwner = ArrayListMultimap.create();

    private Match match;

    @Override
    public void onEnable() {
        this.match = ((MatchGame) this.getGame().getModule(MatchModule.class)).getMatch();
    }

    public void addCapturable(Capturable capturable) {
        this.capturablesById.put(capturable.getId(), capturable);

        if (capturable.hasOwner()) {
            this.capturablesByOwner.put(capturable.getOwner(), capturable);
        }
    }

    public Capturable getCapturable(String id) {
        return this.capturablesById.get(id);
    }

    public Collection<Capturable> getCapturables() {
        return this.capturablesById.values();
    }

    public Collection<Capturable> getCapturables(Participator owner) {
        return this.capturablesByOwner.get(owner);
    }

    public Match getMatch() {
        return this.match;
    }

    public void removeCapturable(Capturable capturable) {
        this.capturablesById.remove(capturable.getId());

        if (capturable.hasOwner()) {
            this.capturablesByOwner.remove(capturable.getOwner(), capturable);
        }
    }

    //
    // Enabling Capturables
    //

    private void enableFlags() {
        // Register flag classes ONLY when there are any flag goals.
        List<Flag> flags = new ArrayList<>();

        for (Capturable capturable : this.getCapturables()) {
            if (capturable instanceof Flag) {
                flags.add((Flag) capturable);
            }
        }

        if (!flags.isEmpty()) {
            this.scheduleSyncTask(new Task(this.getPlugin().getTasks()) {
                @Override
                public void onTick(long ticks) {
                    if (getMatch().isRunning() && ticks % Flag.HEARTBEAT_INTERVAL.toTicks() == 0) {
                        for (Flag flag : flags) {
                            flag.heartbeat(ticks);
                        }
                    }
                }
            });

            this.registerListenerObject(new FlagPayloadRender(this));
            this.registerListenerObject(new FlagPhysicalRender(this));
        }
    }
}
