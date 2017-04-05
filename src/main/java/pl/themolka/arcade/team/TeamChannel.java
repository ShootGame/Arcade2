package pl.themolka.arcade.team;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.channel.ChatChannel;
import pl.themolka.arcade.command.Sender;

import java.util.ArrayList;
import java.util.List;

public class TeamChannel extends ChatChannel implements TeamHolder {
    public static final String TEAM_PERMISSION_NODE = "teamchat";
    public static final String TEAM_FORMAT = ChatColor.DARK_GRAY + "[" +
            ChatColor.GRAY + "%s" + ChatColor.RESET + ChatColor.DARK_GRAY +
            "] " + ChatColor.RESET + "%s" + ChatColor.DARK_GRAY + ": " +
            ChatColor.WHITE + "%s";

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
    public String formatMessage(String author, String message) {
        return String.format(this.getFormat(),
                this.getTeam().getPrettyName(),
                author,
                message);
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
