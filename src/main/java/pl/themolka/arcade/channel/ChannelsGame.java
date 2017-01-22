package pl.themolka.arcade.channel;

import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;

import java.util.List;

public class ChannelsGame extends GameModule {
    private final ChatChannel global;

    public ChannelsGame(ChatChannel global) {
        this.global = global;
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        register.add(new ChannelListeners(this));
        return register;
    }

    public ChatChannel getChannel(Sender sender) {
        GamePlayer player = sender.getGamePlayer();
        if (player != null && player.getCurrentChannel() != null) {
            return player.getCurrentChannel();
        }

        return this.getGlobalChannel();
    }

    public ChatChannel getGlobalChannel() {
        return this.global;
    }
}
