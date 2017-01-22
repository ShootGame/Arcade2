package pl.themolka.arcade.team;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.channel.ChatChannel;
import pl.themolka.arcade.command.Sender;

import java.util.ArrayList;
import java.util.List;

public class TeamChannel extends ChatChannel {
    public static final String TEAM_PERMISSION_NODE = "teamchat";

    private final Team team;

    public TeamChannel(ArcadePlugin plugin, Team team) {
        super(plugin, team.getId());

        this.team = team;
    }

    @Override
    public boolean addMember(Sender member) {
        return false;
    }

    @Override
    public List<Sender> getMembers() {
        return new ArrayList<>(this.getTeam().getOnlineMembers());
    }

    @Override
    public boolean removeMember(Sender member) {
        return false;
    }

    @Override
    public String getPermission() {
        return getPermissionNode(TEAM_PERMISSION_NODE);
    }

    public Team getTeam() {
        return this.team;
    }
}
