package pl.themolka.arcade.lobby;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

@Produces(LobbyGame.Config.class)
public class LobbyGameParser extends GameModuleParser<LobbyGame, LobbyGame.Config> {
    public LobbyGameParser() {
        super(LobbyGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("lobby", "hub");
    }

    @Override
    protected ParserResult<LobbyGame.Config> parsePrimitive(Node node, String name, String value) throws ParserException {
        return ParserResult.fine(node, name, value, new LobbyGame.Config() {});
    }
}
