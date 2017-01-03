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
        ArcadePlayer to = player.getPlayer();

        switch (this.getType()) {
            case ERROR:
                to.sendError(this.getResult());
                break;
            case INFO:
                to.sendInfo(this.getResult());
                break;
            case NORMAL:
                to.send(this.getResult());
                break;
            case SUCCESS:
                to.sendSuccess(this.getResult());
                break;
            case TIP:
                to.sendTip(this.getResult());
                break;
        }
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
            return new MessageContent(XMLParser.parseMessage(xml.getValue()), this.parseType(xml));
        }

        private Type parseType(Element xml) {
            Attribute attribute = xml.getAttribute("type");
            if (attribute != null) {
                return Type.valueOf(XMLParser.parseEnumValue(attribute.getValue()));
            }

            return null;
        }
    }

    public enum Type {
        ERROR, INFO, NORMAL, SUCCESS, TIP
    }
}
