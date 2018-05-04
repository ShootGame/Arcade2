package pl.themolka.arcade.dominator;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Dominator.class)
public class DominatorParser extends ElementParser<Dominator>
                             implements InstallableParser {
    private Parser<DefaultDominators> defaultDominatorParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.defaultDominatorParser = context.type(DefaultDominators.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("dominator strategy");
    }

    @Override
    protected ParserResult<Dominator> parseElement(Element element, String name, String value) throws ParserException {
        return ParserResult.fine(element, name, value, this.defaultDominatorParser.parseWithDefinition(element, name, value).orFail());
    }
}
