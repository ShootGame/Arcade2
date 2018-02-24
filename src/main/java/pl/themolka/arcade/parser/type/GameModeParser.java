package pl.themolka.arcade.parser.type;

import org.bukkit.GameMode;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.AbstractParser;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.number.IntegerParser;

import java.util.List;

public class GameModeParser extends AbstractParser<GameMode>
                            implements InstallableParser {
    private EnumParser<GameMode> enumParser;
    private IntegerParser integerParser;

    @Override
    public void install(ParserContext context) {
        this.enumParser = context.enumType(GameMode.class);
        this.integerParser = context.of(IntegerParser.class);
    }

    @Override
    public List<Object> expect() {
        return this.enumParser.expectedValues();
    }

    @Override
    protected ParserResult<GameMode> parse(Element element, String name, String value) throws ParserException {
        // Try to find legacy ID first.
        Integer id = this.integerParser.parseWithDefinition(element, name, value).orNull();
        if (id != null) {
            GameMode gameMode = GameMode.getByValue(id);

            if (gameMode != null) {
                return ParserResult.fine(element, name, value, gameMode);
            }
        }

        return this.enumParser.parseWithDefinition(element, name, value);
    }
}
