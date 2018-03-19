package pl.themolka.arcade.kit.content;

import org.bukkit.entity.Player;
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
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.xml.XMLParser;

public class FlyContent implements RemovableKitContent<Boolean> {
    public static final boolean DEFAULT_ALLOW_FLYING = false;
    public static final boolean DEFAULT_FLYING = false;

    private final boolean result;

    public FlyContent(boolean result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Boolean value) {
        Player bukkit = player.getBukkit();
        bukkit.setAllowFlight(value);

        if (!value) {
            bukkit.setFlying(false);
        }
    }

    @Override
    public Boolean defaultValue() {
        return DEFAULT_ALLOW_FLYING;
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    public static class LegacyParser implements LegacyKitContentParser<FlyContent> {
        @Override
        public FlyContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null) {
                return new FlyContent(DEFAULT_FLYING);
            }

            return new FlyContent(XMLParser.parseBoolean(xml.getValue(), DEFAULT_FLYING));
        }
    }

    @NestedParserName({"fly", "flying"})
    @Produces(FlyContent.class)
    public static class ContentParser extends BaseRemovableContentParser<FlyContent>
                                      implements InstallableParser {
        private Parser<Boolean> flyingParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.flyingParser = context.type(Boolean.class);
        }

        @Override
        protected ParserResult<FlyContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new FlyContent(DEFAULT_FLYING));
            }

            boolean flying = this.flyingParser.parse(node).orFail();
            return ParserResult.fine(node, name, value, new FlyContent(flying));
        }
    }
}
