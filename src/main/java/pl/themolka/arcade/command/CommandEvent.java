package pl.themolka.arcade.command;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameEvent;
import pl.themolka.arcade.session.ArcadeSession;
import pl.themolka.commons.command.CommandContext;

public class CommandEvent extends GameEvent {
    private final ArcadeSession sender;
    private final CommandContext context;

    public CommandEvent(ArcadePlugin plugin, Game game, ArcadeSession sender, CommandContext context) {
        super(plugin, game);

        this.sender = sender;
        this.context = context;
    }

    public ArcadeSession getSender() {
        return this.sender;
    }

    public CommandContext getContext() {
        return this.context;
    }
}
