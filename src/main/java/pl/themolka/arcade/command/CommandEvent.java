package pl.themolka.arcade.command;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class CommandEvent extends GameEvent {
    private final Sender sender;
    private final CommandContext context;

    public CommandEvent(ArcadePlugin plugin, Sender sender, CommandContext context) {
        super(plugin);

        this.sender = sender;
        this.context = context;
    }

    public Sender getSender() {
        return this.sender;
    }

    public CommandContext getContext() {
        return this.context;
    }
}
