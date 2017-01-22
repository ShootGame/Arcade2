package pl.themolka.arcade.channel;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

public class ChannelListeners implements Listener {
    public static final String CHANNEL_SPY_PERMISSION = "arcade.channel.spy";

    private final ChannelsGame game;

    public ChannelListeners(ChannelsGame game) {
        this.game = game;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        ArcadePlayer player = this.game.getPlugin().getPlayer(event.getPlayer());
        ChatChannel channel = this.game.getChannel(player);

        String message = event.getMessage();
        if (message.startsWith(GlobalChatChannel.GLOBAL_CHANNEL_KEY)) { // global
            channel = this.game.getGlobalChannel();
            message = message.substring(1);
        }

        channel.sendChat(player, message);
        event.setCancelled(true);
    }

    @Handler(priority = Priority.LAST)
    public void onChannelSpy(ChannelChatEvent event) {
        if (event.isCanceled()) {
            return;
        }

        String message = ChatColor.DARK_AQUA + ChatColor.ITALIC.toString() + "[Spy]" + ChatColor.RESET + " " +
                ChatColor.GRAY + event.getAuthorName() + ChatColor.RESET + ChatColor.GRAY + ": " + event.getMessage();
        for (GamePlayer player : this.game.getGame().getPlayers()) {
            if (player.isOnline() && !player.isParticipating() &&
                    player.hasPermission(CHANNEL_SPY_PERMISSION) && !(player.getCurrentChannel() instanceof GlobalChatChannel)) {
                player.sendChat(message);
            }
        }
    }
}
