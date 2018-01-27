package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class MessageContent implements KitContent<String> {
    private final String result;
    private final Type type;

    public MessageContent(String result) {
        this(result, null);
    }

    public MessageContent(String result, Type type) {
        this.result = result;

        if (type != null) {
            this.type = type;
        } else {
            this.type = Type.NORMAL;
        }
    }

    @Override
    public void apply(GamePlayer player) {
        this.getType().sendMessage(player, this.getResult());
    }

    @Override
    public String getResult() {
        return this.result;
    }

    public Type getType() {
        return this.type;
    }

    public static class Parser implements KitContentParser<MessageContent> {
        @Override
        public MessageContent parse(Element xml) throws DataConversionException {
            return new MessageContent(XMLParser.parseMessage(xml.getTextNormalize()), this.parseType(xml));
        }

        private Type parseType(Element xml) {
            Attribute attribute = xml.getAttribute("channel");
            if (attribute != null) {
                return Type.valueOf(XMLParser.parseEnumValue(attribute.getValue()));
            }

            return null;
        }
    }

    public enum Type {
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
    }
}
