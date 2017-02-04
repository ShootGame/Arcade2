package pl.themolka.arcade.event;

import net.engio.mbassy.bus.common.DeadMessage;
import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.GameCommands;

public class DeadListeners {
    private final ArcadePlugin plugin;

    public DeadListeners(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Handler(priority = Priority.LAST)
    public void onDeadMessage(DeadMessage event) {
        Object dead = event.getMessage();
        if (dead instanceof GameCommands.GameCommandEvent) {
            this.onGameCommand((GameCommands.GameCommandEvent) dead);
        } else if (dead instanceof GameCommands.JoinCommandEvent) {
            this.onJoinCommand((GameCommands.JoinCommandEvent) dead);
        } else if (dead instanceof GameCommands.LeaveCommandEvent) {
            this.onLeaveCommand((GameCommands.LeaveCommandEvent) dead);
        }
    }

    private void onGameCommand(GameCommands.GameCommandEvent event) {
        event.getSender().sendError("No game results to display.");
    }

    private void onJoinCommand(GameCommands.JoinCommandEvent event) {
        event.getSender().sendError("Could not join the game: no format to listen.");
    }

    private void onLeaveCommand(GameCommands.LeaveCommandEvent event) {
        event.getSender().sendError("Could not leave the game: no format to listen.");
    }
}
