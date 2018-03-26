package pl.themolka.arcade.filter;

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

@Produces(FiltersGame.class)
public class FiltersGameParser extends GameModuleParser<FiltersGame, FiltersGame.Config>
                               implements InstallableParser {
    private Parser<FilterSet> filterSetParser;

    public FiltersGameParser() {
        super(FiltersGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("filters", "filter");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.filterSetParser = context.type(FilterSet.class);
    }

    @Override
    protected ParserResult<FiltersGame.Config> parseTree(Node node, String name) throws ParserException {
        Set<FilterSet> filterSets = new LinkedHashSet<>();
        for (Node filterSetNode : node.children("filter")) {
            filterSets.add(this.filterSetParser.parse(filterSetNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(filterSets)) {
            throw this.fail(node, name, null, "No filter groups defined");
        }

        return ParserResult.fine(node, name, null, new FiltersGame.Config() {
            public Set<FilterSet> filterSets() { return filterSets; }
        });
    }
}
