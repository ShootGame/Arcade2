package pl.themolka.arcade.channel;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.Sender;

import java.util.ArrayList;
import java.util.List;

public class GlobalChatChannel extends ChatChannel {
    public static final String GLOBAL_CHANNEL_ID = "_global";
    public static final String GLOBAL_CHANNEL_KEY = "!";
    public static final String GLOBAL_FORMAT = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + ChatColor.BOLD + "G" + ChatColor.RESET +
            ChatColor.DARK_GRAY + "] " + ChatColor.RESET + "%s" + ChatColor.DARK_GRAY + ": " +  ChatColor.WHITE + "%s";
    public static final String GLOBAL_PERMISSION_NODE = getPermissionNode("global");

    private final ArcadePlugin plugin;

    private String format;

    public GlobalChatChannel(ArcadePlugin plugin, String format) {
        super(plugin, GLOBAL_CHANNEL_ID);
        this.plugin = plugin;

        this.format = format;
    }

    @Override
    public boolean addMember(Sender member) {
        return false;
    }

    @Override
    public String getFormat() {
        return this.format;
    }

    @Override
    public List<Sender> getMembers() {
        return new ArrayList<>(this.plugin.getPlayers());
    }

    @Override
    public String getPermission() {
        return GLOBAL_PERMISSION_NODE;
    }

    @Override
    public boolean removeMember(Sender member) {
        return false;
    }

    @Override
    public void setFormat(String format) {
        throw new UnsupportedOperationException("Not supported here.");
    }
}
