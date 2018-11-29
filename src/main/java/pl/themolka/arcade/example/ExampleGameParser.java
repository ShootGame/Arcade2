package pl.themolka.arcade.example;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

@Produces(ExampleGame.Config.class)
public class ExampleGameParser extends GameModuleParser<ExampleGame, ExampleGame.Config> {
    public ExampleGameParser() {
        super(ExampleGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("example");
    }

    @Override
    protected Result<ExampleGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        return Result.fine(node, name, value, new ExampleGame.Config() {});
    }
}
