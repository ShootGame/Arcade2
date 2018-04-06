package pl.themolka.arcade.channel;

import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.List;

public class ChannelsGame extends GameModule {
    private final ChatChannel global;

    protected ChannelsGame(Game game, Config config) {
        this.global = new GlobalChatChannel(game.getPlugin(), GlobalChatChannel.GLOBAL_FORMAT);
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        register.add(new ChannelListeners(this));
        return register;
    }

    public ChatChannel getChannelFor(Sender sender) {
        GamePlayer player = sender.getGamePlayer();
        if (player != null && player.getCurrentChannel() != null) {
            return player.getCurrentChannel();
        }

        return this.getGlobalChannel();
    }

    public ChatChannel getGlobalChannel() {
        return this.global;
    }

    public interface Config extends IGameModuleConfig<ChannelsGame> {
        @Override
        default ChannelsGame create(Game game, Library library) {
            return new ChannelsGame(game, this);
        }
    }
}
