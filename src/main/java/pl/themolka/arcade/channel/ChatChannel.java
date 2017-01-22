package pl.themolka.arcade.channel;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.ConsoleSender;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class ChatChannel extends AbstractChannel {
    public static final String EMPTY_MESSAGE = "No message specified.";
    public static final String PERMISSION_ERROR = "You don't have permission to write in this channel.";
    public static final String PERMISSION_NODE = "arcade.channel";

    private final ArcadePlugin plugin;

    private String format;
    private final List<Sender> members = new ArrayList<>();

    public ChatChannel(ArcadePlugin plugin, String id) {
        super(id);
        this.plugin = plugin;
    }

    @Override
    public boolean addMember(Sender member) {
        return this.members.add(member);
    }

    @Override
    public List<Sender> getMembers() {
        return this.members;
    }

    @Override
    public String getPermission() {
        return getPermissionNode(super.getPermission());
    }

    @Override
    public boolean removeMember(Sender member) {
        return this.members.remove(member);
    }

    @Override
    public void sendChat(Sender author, String message) {
        if (this.getPermission() != null && !author.hasPermission(this.getPermission())) {
            author.sendError(PERMISSION_ERROR);
            return;
        } else if (message.isEmpty()) {
            author.sendError(EMPTY_MESSAGE);
            return;
        }

        String name = author.getUsername();
        if (author.isConsole()) {
            name = ChatColor.DARK_RED + ChatColor.ITALIC.toString() + ConsoleSender.CONSOLE_NAME;
        } else {
            GamePlayer player = author.getGamePlayer();
            if (player.hasDisplayName()) {
                name = player.getDisplayName();
            }
        }

        // event
        ChannelChatEvent event = new ChannelChatEvent(this.plugin, this, author, name, message);
        this.plugin.getEventBus().publish(event);

        if (!event.isCanceled()) {
            this.sendMessage(event.getAuthorName(), event.getMessage());
        }
    }

    public String formatMessage(String author, String message) {
        return String.format(this.getFormat(), author, message);
    }

    public String getFormat() {
        return this.format;
    }

    public void sendMessage(String author, String message) {
        this.plugin.getLogger().info(String.format("[Chat '%s'] %s: %s", this.getId(), ChatColor.stripColor(author), message));
        this.sendChat(this.formatMessage(author, message));
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public static String getPermissionNode(String node) {
        return PERMISSION_NODE + "." + node;
    }
}
