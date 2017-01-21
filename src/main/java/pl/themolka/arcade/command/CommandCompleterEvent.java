package pl.themolka.arcade.command;

import pl.themolka.arcade.ArcadePlugin;

import java.util.ArrayList;
import java.util.List;

public class CommandCompleterEvent extends CommandEvent {
    private final List<String> results = new ArrayList<>();

    public CommandCompleterEvent(ArcadePlugin plugin, Sender sender, CommandContext context) {
        this(plugin, sender, context, null);
    }

    public CommandCompleterEvent(ArcadePlugin plugin, Sender sender, CommandContext context, List<String> results) {
        super(plugin, sender, context);

        if (results != null) {
            this.results.addAll(results);
        }
    }

    public boolean addResult(String result) {
        return this.results.add(result);
    }

    public List<String> getResults() {
        return this.results;
    }
}
