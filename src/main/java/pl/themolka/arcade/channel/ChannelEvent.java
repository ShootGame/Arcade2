package pl.themolka.arcade.channel;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class ChannelEvent extends GameEvent {
    private final Channel channel;

    public ChannelEvent(ArcadePlugin plugin, Channel channel) {
        super(plugin);

        this.channel = channel;
    }

    public Channel getChannel() {
        return this.channel;
    }
}
