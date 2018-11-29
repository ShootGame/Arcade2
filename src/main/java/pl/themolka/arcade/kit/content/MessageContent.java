package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.session.ArcadePlayer;

public class MessageContent implements KitContent<String> {
    private final String result;
    private final Channel channel;

    protected MessageContent(Config config) {
        this.result = config.result().get();
        this.channel = config.channel().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void apply(GamePlayer player) {
        this.channel.sendMessage(player, this.result);
    }

    @Override
    public String getResult() {
        return this.result;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public enum Channel {
        ACTION_BAR {
            @Override
            public void sendMessage(ArcadePlayer player, String message) {
                player.sendAction(message);
            }
        },

        CHAT { // may be hidden
            @Override
            public void sendMessage(ArcadePlayer player, String message) {
                player.sendChat(message);
            }
        },

        ERROR {
            @Override
            public void sendMessage(ArcadePlayer player, String message) {
                player.sendError(message);
            }
        },

        INFO {
            @Override
            public void sendMessage(ArcadePlayer player, String message) {
                player.sendInfo(message);
            }
        },

        NORMAL { // shown
            @Override
            public void sendMessage(ArcadePlayer player, String message) {
                player.send(message);
            }
        },

        SUCCESS {
            @Override
            public void sendMessage(ArcadePlayer player, String message) {
                player.sendSuccess(message);
            }
        },

        TIP {
            @Override
            public void sendMessage(ArcadePlayer player, String message) {
                player.sendTip(message);
            }
        },
        ;

        public abstract void sendMessage(ArcadePlayer player, String message);

        public void sendMessage(GamePlayer player, String message) {
            if (player != null && player.isOnline()) {
                this.sendMessage(player.getPlayer(), message);
            }
        }

        public static Channel getDefault() {
            return NORMAL;
        }

        public static Channel nonNull(Channel channel) {
            return channel != null ? channel : getDefault();
        }
    }

    @NestedParserName({"message", "msg", "send", "announce"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<String> textParser;
        private Parser<Channel> channelParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.textParser = context.type(String.class);
            this.channelParser = context.type(Channel.class);
        }

        @Override
        protected Result<Config> parsePrimitive(Node node, String name, String value) throws ParserException {
            String text = this.textParser.parseWithDefinition(node, name, value).orFail();
            Channel channel = this.channelParser.parse(node.property("channel")).orDefault(Config.DEFAULT_CHANNEL);

            return Result.fine(node, name, value, new Config() {
                public Ref<String> result() { return Ref.ofProvided(text); }
                public Ref<Channel> channel() { return Ref.ofProvided(channel); }
            });
        }
    }

    public interface Config extends KitContent.Config<MessageContent, String> {
        Channel DEFAULT_CHANNEL = Channel.getDefault();

        default Ref<Channel> channel() { return Ref.ofProvided(DEFAULT_CHANNEL); }

        @Override
        default MessageContent create(Game game, Library library) {
            return new MessageContent(this);
        }
    }
}
