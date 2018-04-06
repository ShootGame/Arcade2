package pl.themolka.arcade.team;

import org.bukkit.ChatColor;
import pl.themolka.arcade.channel.ChatChannel;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.Game;

import java.util.ArrayList;
import java.util.List;

public class TeamChannel extends ChatChannel implements TeamHolder {
    public static final String TEAM_PERMISSION_NODE = "teamchat";
    public static final String TEAM_FORMAT = ChatColor.DARK_GRAY + "[" +
            ChatColor.GRAY + "TEAM" + ChatColor.RESET + ChatColor.DARK_GRAY +
            "] " + ChatColor.YELLOW + "%s" + ChatColor.DARK_GRAY + ": " +
            ChatColor.WHITE + "%s";

    private final Team team;

    public TeamChannel(Game game, Team team) {
        super(game.getPlugin(), team.getId());

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
    public String getPermission() {
        return getPermissionNode(TEAM_PERMISSION_NODE);
    }

    @Override
    public Team getTeam() {
        return this.team;
    }

    @Override
    public boolean removeMember(Sender member) {
        return false;
    }
}
