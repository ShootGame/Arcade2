package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(ScoreboardInfo.class)
public class ScoreboardInfoParser extends NodeParser<ScoreboardInfo>
                                  implements InstallableParser {
    private Parser<String> titleParser;

    @Override
    public void install(ParserContext context) {
        this.titleParser = context.type(String.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("scoreboard information");
    }

    @Override
    protected ParserResult<ScoreboardInfo> parseTree(Node node, String name) throws ParserException {
        ScoreboardInfo info = new ScoreboardInfo();
        info.setTitle(this.titleParser.parse(node.property("title")).orDefaultNull());
        return ParserResult.fine(node, name, info);
    }
}
