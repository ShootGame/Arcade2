package pl.themolka.arcade.mob;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(MobSpawnRule.Config.class)
public class MobSpawnRuleParser extends ConfigParser<MobSpawnRule.Config>
                                implements InstallableParser {
    private Parser<Ref> filterParser;
    private Parser<Boolean> allowParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("mob spawn rule");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.filterParser = context.type(Ref.class);
        this.allowParser = context.type(Boolean.class);
    }

    @Override
    protected ParserResult<MobSpawnRule.Config> parsePrimitive(Node node, String name, String value) throws ParserException {
        String id = this.parseOptionalId(node);
        Ref<Filter.Config<?>> filter = this.filterParser.parse(node.property("filter")).orFail();
        boolean allow = this.allowParser.parseWithDefinition(node, name, value).orFail();

        return ParserResult.fine(node, name, value, new MobSpawnRule.Config() {
            public String id() { return id; }
            public Ref<Filter.Config<?>> filter() { return filter; }
            public boolean cancel() { return !allow; }
        });
    }
}
