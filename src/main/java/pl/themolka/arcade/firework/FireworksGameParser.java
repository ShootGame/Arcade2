package pl.themolka.arcade.firework;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

@Produces(FireworksGame.Config.class)
public class FireworksGameParser extends GameModuleParser<FireworksGame, FireworksGame.Config>
                                 implements InstallableParser {
    private Parser<Boolean> enabledParser;

    public FireworksGameParser() {
        super(FireworksGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("fireworks");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.enabledParser = context.type(Boolean.class);
    }

    @Override
    protected ParserResult<FireworksGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        boolean onCoreLeak = this.enabledParser.parse(node.property("core-leak")).orDefault(true);
        boolean onPointCapture = this.enabledParser.parse(node.property("point-capture")).orDefault(true);
        boolean onWoolPlace = this.enabledParser.parse(node.property("wool-place")).orDefault(true);

        if (!onCoreLeak && !onPointCapture && !onWoolPlace) {
            return ParserResult.empty(node, name);
        }

        return ParserResult.fine(node, name, value, new FireworksGame.Config() {
            public Ref<Boolean> onCoreLeak() { return Ref.ofProvided(onCoreLeak); }
            public Ref<Boolean> onPointCapture() { return Ref.ofProvided(onPointCapture); }
            public Ref<Boolean> onWoolPlace() { return Ref.ofProvided(onWoolPlace); }
        });
    }
}
