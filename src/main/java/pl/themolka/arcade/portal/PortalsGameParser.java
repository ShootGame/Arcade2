package pl.themolka.arcade.portal;

import pl.themolka.arcade.config.Ref;
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

@Produces(PortalsGame.Config.class)
public class PortalsGameParser extends GameModuleParser<PortalsGame, PortalsGame.Config>
                               implements InstallableParser {
    private Parser<Portal.Config> portalParser;

    public PortalsGameParser() {
        super(PortalsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("portals", "portal");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.portalParser = context.type(Portal.Config.class);
    }

    @Override
    protected ParserResult<PortalsGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        Set<Portal.Config> portals = new LinkedHashSet<>();
        for (Node portalNode : node.children("portal")) {
            portals.add(this.portalParser.parse(portalNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(portals)) {
            throw this.fail(node, name, value, "No portals defined");
        }

        return ParserResult.fine(node, name, value, new PortalsGame.Config() {
            public Ref<Set<Portal.Config>> portals() { return Ref.ofProvided(portals); }
        });
    }
}
