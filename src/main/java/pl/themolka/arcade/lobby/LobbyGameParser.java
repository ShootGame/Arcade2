package pl.themolka.arcade.lobby;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

@Produces(LobbyGame.Config.class)
public class LobbyGameParser extends GameModuleParser<LobbyGame, LobbyGame.Config>
                             implements InstallableParser {
    private Parser<Boolean> enabledParser;

    public LobbyGameParser() {
        super(LobbyGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("lobby", "hub");
    }

    @Override
    public void install(ParserContext context) {
        this.enabledParser = context.type(Boolean.class);
    }

    @Override
    protected ParserResult<LobbyGame.Config> parsePrimitive(Node node, String name, String value) throws ParserException {
        boolean enabled = this.enabledParser.parseWithDefinition(node, name, value).orFail();

        return ParserResult.fine(node, name, value, new LobbyGame.Config() {
            public boolean enabled() { return enabled; }
        });
    }
}
