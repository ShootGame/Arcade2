package pl.themolka.arcade.parser.type;

import org.bukkit.Difficulty;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Set;

@Produces(Difficulty.class)
public class DifficultyParser extends ElementParser<Difficulty>
                              implements InstallableParser {
    private Parser<Integer> idParser;
    private Parser<Difficulty> difficultyParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.idParser = context.type(Integer.class);
        this.difficultyParser = context.enumType(Difficulty.class);
    }

    @Override
    public Set<Object> expect() {
        return this.difficultyParser.expect();
    }

    @Override
    protected ParserResult<Difficulty> parseElement(Element element, String name, String value) throws ParserException {
        // Try to find legacy ID first.
        Integer id = this.idParser.parseWithDefinition(element, name, value).orNull();
        if (id != null) {
            Difficulty difficulty = Difficulty.getByValue(id);

            if (difficulty != null) {
                return ParserResult.fine(element, name, value, difficulty);
            }
        }

        return this.difficultyParser.parseWithDefinition(element, name, value);
    }
}
