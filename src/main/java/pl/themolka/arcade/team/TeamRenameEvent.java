package pl.themolka.arcade.team;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class TeamRenameEvent extends GameEvent {
    private final String oldName;
    private final Team team;

    public TeamRenameEvent(ArcadePlugin plugin, String oldName, Team team) {
        super(plugin);

        this.oldName = oldName;
        this.team = team;
    }

    public String getNewName() {
        return this.getTeam().getName();
    }

    public String getOldName() {
        return this.oldName;
    }

    public Team getTeam() {
        return this.team;
    }
}
