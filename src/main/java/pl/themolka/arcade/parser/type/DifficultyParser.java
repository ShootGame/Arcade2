package pl.themolka.arcade.parser.type;

import org.bukkit.Difficulty;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.AbstractParser;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.number.IntegerParser;

import java.util.List;

@Produces(Difficulty.class)
public class DifficultyParser extends AbstractParser<Difficulty>
                              implements InstallableParser {
    private EnumParser<Difficulty> difficultyParser;
    private IntegerParser integerParser;

    @Override
    public void install(ParserContext context) {
        this.difficultyParser = context.enumType(Difficulty.class);
        this.integerParser = context.of(IntegerParser.class);
    }

    @Override
    public List<Object> expect() {
        return this.difficultyParser.expectedValues();
    }

    @Override
    protected ParserResult<Difficulty> parse(Element element, String name, String value) throws ParserException {
        // Try to find legacy ID first.
        Integer id = this.integerParser.parseWithDefinition(element, name, value).orNull();
        if (id != null) {
            Difficulty difficulty = Difficulty.getByValue(id);

            if (difficulty != null) {
                return ParserResult.fine(element, name, value, difficulty);
            }
        }

        return this.difficultyParser.parseWithDefinition(element, name, value);
    }
}
