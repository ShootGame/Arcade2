package pl.themolka.arcade.channel;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

@Produces(ChannelsGame.Config.class)
public class ChannelsGameParser extends GameModuleParser<ChannelsGame, ChannelsGame.Config> {
    public ChannelsGameParser() {
        super(ChannelsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("channels", "chat-channels", "chatchannels");
    }

    @Override
    protected ParserResult<ChannelsGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        return ParserResult.fine(node, name, value, new ChannelsGame.Config() {});
    }
}
