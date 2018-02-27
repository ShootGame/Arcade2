package pl.themolka.arcade.parser.type;

import org.bukkit.GameMode;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.List;

@Produces(GameMode.class)
public class GameModeParser extends ElementParser<GameMode>
                            implements InstallableParser {
    private Parser<GameMode> gameModeParser;
    private Parser<Integer> integerParser;

    @Override
    public void install(ParserContext context) {
        this.gameModeParser = context.enumType(GameMode.class);
        this.integerParser = context.type(Integer.class);
    }

    @Override
    public List<Object> expect() {
        return this.gameModeParser.expect();
    }

    @Override
    protected ParserResult<GameMode> parseElement(Element element, String name, String value) throws ParserException {
        // Try to find legacy ID first.
        Integer id = this.integerParser.parseWithDefinition(element, name, value).orNull();
        if (id != null) {
            GameMode gameMode = GameMode.getByValue(id);

            if (gameMode != null) {
                return ParserResult.fine(element, name, value, gameMode);
            }
        }

        return this.gameModeParser.parseWithDefinition(element, name, value);
    }
}
