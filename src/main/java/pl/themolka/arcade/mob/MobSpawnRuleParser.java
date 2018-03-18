package pl.themolka.arcade.mob;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(MobSpawnRule.Config.class)
public class MobSpawnRuleParser extends NodeParser<MobSpawnRule.Config>
                                implements InstallableParser {
    private Parser<Ref> filterParser;
    private Parser<Boolean> allowParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("mob spawn rule");
    }

    @Override
    public void install(ParserContext context) {
        this.filterParser = context.type(Ref.class);
        this.allowParser = context.type(Boolean.class);
    }

    @Override
    protected ParserResult<MobSpawnRule.Config> parsePrimitive(Node node, String name, String value) throws ParserException {
        Ref<Filter> filter = this.filterParser.parse(node.property("filter")).orFail();
        boolean allow = this.allowParser.parse(node).orFail();

        return ParserResult.fine(node, name, value, new MobSpawnRule.Config() {
            public Ref<Filter> filter() { return filter; }
            public boolean cancel() { return !allow; }
        });
    }
}
