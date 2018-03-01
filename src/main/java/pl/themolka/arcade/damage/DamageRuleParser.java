package pl.themolka.arcade.damage;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.type.PercentageParser;
import pl.themolka.arcade.util.Percentage;

import java.util.Collections;
import java.util.Set;

@Produces(DamageRule.Config.class)
public class DamageRuleParser extends ConfigParser<DamageRule.Config>
                              implements InstallableParser {
    private Parser<Ref> entityFilterParser;
    private Parser<Ref> playerFilterParser;
    private Parser<Boolean> denyParser;
    private Parser<Double> damageParser;
    private Parser<Percentage> multiplierParser;

    @Override
    public void install(ParserContext context) {
        super.install(context);
        this.entityFilterParser = context.type(Ref.class);
        this.playerFilterParser = context.type(Ref.class);
        this.denyParser = context.type(Boolean.class);
        this.damageParser = context.type(Double.class);
        this.multiplierParser = context.of(PercentageParser.Infinite.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("damage rule");
    }

    @Override
    protected ParserResult<DamageRule.Config> parsePrimitive(Node node, String name, String value) throws ParserException {
        String id = this.parseOptionalId(node);
        Ref<Filter> entityFilter = this.entityFilterParser.parse(node.property("entity-filter", "filter")).orDefaultNull();
        Ref<Filter> playerFilter = this.playerFilterParser.parse(node.property("player-filter", "filter")).orDefaultNull();

        boolean notDenied = this.denyParser.parse(node).orDefault(true);
        double damage = notDenied ? this.damageParser.parse(node).orFail() : DamageRule.DENY_DAMAGE;
        Percentage multiplier = this.multiplierParser.parse(node.property("multiplier", "multiply")).orDefault(Percentage.DONE);

        return ParserResult.fine(node, name, value, new DamageRule.Config() {
            public String id() { return id; }
            public Ref<Filter> entityFilter() { return entityFilter; }
            public Ref<Filter> playerFilter() { return playerFilter; }
            public double damage() { return damage; }
            public Percentage multiplier() { return multiplier; }
        });
    }
}
