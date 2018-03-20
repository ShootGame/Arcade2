package pl.themolka.arcade.kit;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;

import java.util.LinkedHashSet;
import java.util.Set;

@Produces(KitsGame.Config.class)
public class KitsGameParser extends GameModuleParser<KitsGame, KitsGame.Config>
                            implements InstallableParser {
    private Parser<Kit.Config> kitParser;

    public KitsGameParser() {
        super(KitsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("kits", "kit");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.kitParser = context.type(Kit.Config.class);
    }

    @Override
    protected ParserResult<KitsGame.Config> parseTree(Node node, String name) throws ParserException {
        Set<Kit.Config> kits = new LinkedHashSet<>();
        for (Node kitNode : node.children("kit", "set", "package")) {
            kits.add(this.kitParser.parse(kitNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(kits)) {
            throw this.fail(node, name, null, "No kits defined");
        }

        return ParserResult.fine(node, name, new KitsGame.Config() {
            public Set<Kit.Config> kits() { return kits; }
        });
    }
}
