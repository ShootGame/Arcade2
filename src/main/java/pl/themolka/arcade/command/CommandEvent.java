package pl.themolka.arcade.command;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameEvent;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.session.Session;

public class CommandEvent extends GameEvent {
    private final Session<ArcadePlayer> sender;
    private final CommandContext context;

    public CommandEvent(ArcadePlugin plugin, Game game, Session<ArcadePlayer> sender, CommandContext context) {
        super(plugin, game);

        this.sender = sender;
        this.context = context;
    }

    public Session<ArcadePlayer> getSender() {
        return this.sender;
    }

    public CommandContext getContext() {
        return this.context;
    }
}
