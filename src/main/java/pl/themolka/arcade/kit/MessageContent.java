package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class MessageContent implements KitContent<String> {
    private final String result;
    private final Channel channel;

    public MessageContent(String result) {
        this(result, null);
    }

    public MessageContent(String result, Channel channel) {
        this.result = result;
        this.channel = Channel.nonNull(channel);
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void apply(GamePlayer player) {
        this.getChannel().sendMessage(player, this.getResult());
    }

    @Override
    public String getResult() {
        return this.result;
    }

    public Channel getChannel() {
        return this.channel;
    }

    @KitContentLegacyParser
    public static class LegacyParser implements KitContentParser<MessageContent> {
        @Override
        public MessageContent parse(Element xml) throws DataConversionException {
            return new MessageContent(XMLParser.parseMessage(xml.getValue()), this.parseChannel(xml));
        }

        private Channel parseChannel(Element xml) {
            Attribute attribute = xml.getAttribute("channel");
            if (attribute != null) {
                return Channel.valueOf(XMLParser.parseEnumValue(attribute.getValue()));
            }

            return null;
        }
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
    @Produces(MessageContent.class)
    public static class ContentParser extends BaseContentParser<MessageContent>
                                      implements InstallableParser {
        private Parser<String> textParser;
        private Parser<Channel> channelParser;

        @Override
        public void install(ParserContext context) {
            this.textParser = context.type(String.class);
            this.channelParser = context.enumType(Channel.class);
        }

        @Override
        protected ParserResult<MessageContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            String text = this.textParser.parse(node).orFail();
            Channel channel = this.channelParser.parse(node.property("channel")).orDefault(Channel.getDefault());
            return ParserResult.fine(node, name, value, new MessageContent(text, channel));
        }
    }
}
