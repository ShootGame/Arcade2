package pl.themolka.arcade.kit.content;

import org.bukkit.GameMode;
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
import pl.themolka.arcade.xml.XMLGameMode;

public class GameModeContent implements KitContent<GameMode>  {
    public static final GameMode DEFAULT_GAME_MODE = GameMode.SURVIVAL;

    private final GameMode result;

    public GameModeContent(GameMode result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().setGameMode(this.getResult());
    }

    @Override
    public GameMode getResult() {
        return this.result;
    }

    public static class LegacyParser implements LegacyKitContentParser<GameModeContent> {
        @Override
        public GameModeContent parse(Element xml) throws DataConversionException {
            return new GameModeContent(XMLGameMode.parse(xml.getValue()));
        }
    }

    @NestedParserName({"gamemode", "game-mode", "gm"})
    @Produces(GameModeContent.class)
    public static class ContentParser extends BaseContentParser<GameModeContent>
                                      implements InstallableParser {
        private Parser<GameMode> gameModeParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.gameModeParser = context.type(GameMode.class);
        }

        @Override
        protected ParserResult<GameModeContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            GameMode gameMode = this.gameModeParser.parse(node).orFail();
            return ParserResult.fine(node, name, value, new GameModeContent(gameMode));
        }
    }
}
