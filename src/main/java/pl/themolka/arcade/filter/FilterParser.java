package pl.themolka.arcade.filter;

import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.EntityType;
import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.matcher.BaseMatcherParser;
import pl.themolka.arcade.filter.matcher.CanFlyMatcher;
import pl.themolka.arcade.filter.matcher.ChanceMatcher;
import pl.themolka.arcade.filter.matcher.CrouchingMatcher;
import pl.themolka.arcade.filter.matcher.DamageMatcher;
import pl.themolka.arcade.filter.matcher.DeadMatcher;
import pl.themolka.arcade.filter.matcher.EliminatedMatcher;
import pl.themolka.arcade.filter.matcher.EntityMatcher;
import pl.themolka.arcade.filter.matcher.FlyMatcher;
import pl.themolka.arcade.filter.matcher.GlowingMatcher;
import pl.themolka.arcade.filter.matcher.MaterialMatcher;
import pl.themolka.arcade.filter.matcher.OnGroundMatcher;
import pl.themolka.arcade.filter.matcher.ParticipatingMatcher;
import pl.themolka.arcade.filter.matcher.SpawnReasonMatcher;
import pl.themolka.arcade.filter.matcher.SprintingMatcher;
import pl.themolka.arcade.filter.matcher.TeamMatcher;
import pl.themolka.arcade.filter.matcher.VoidMatcher;
import pl.themolka.arcade.filter.operator.AndOperator;
import pl.themolka.arcade.filter.operator.NandOperator;
import pl.themolka.arcade.filter.operator.NoneOperator;
import pl.themolka.arcade.filter.operator.NorOperator;
import pl.themolka.arcade.filter.operator.NotOperator;
import pl.themolka.arcade.filter.operator.OrOperator;
import pl.themolka.arcade.filter.operator.XnorOperator;
import pl.themolka.arcade.filter.operator.XorOperator;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserMap;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.parser.Silent;

import java.util.Collections;
import java.util.Set;

@Produces(Filter.Config.class)
public class FilterParser extends ConfigParser<Filter.Config<?>>
                          implements InstallableParser {
    private static final Set<Class<?>> types = ImmutableSet.<Class<?>>builder()
            .add(FilterParser.class) // this class
            // matchers
            .add(CanFlyMatcher.class)
            .add(ChanceMatcher.class)
            .add(CrouchingMatcher.class)
            .add(DamageMatcher.class)
            .add(DeadMatcher.class)
            .add(EliminatedMatcher.class)
            .add(EntityMatcher.class)
            .add(FlyMatcher.class)
            .add(GlowingMatcher.class)
            .add(MaterialMatcher.class)
            .add(OnGroundMatcher.class)
            .add(ParticipatingMatcher.class)
            .add(SpawnReasonMatcher.class)
            .add(SprintingMatcher.class)
            .add(TeamMatcher.class)
            .add(VoidMatcher.class)
            // operators
            .add(AndOperator.class)
            .add(NandOperator.class)
            .add(NoneOperator.class)
            .add(NorOperator.class)
            .add(NotOperator.class)
            .add(OrOperator.class)
            .add(XnorOperator.class)
            .add(XorOperator.class)
            .build();

    private NestedParserMap<BaseFilterParser<?>> nested;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("filter");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);

        this.nested = new NestedParserMap<>(context);
        for (Class<?> clazz : types) {
            this.nested.scan(clazz);
        }
    }

    @Override
    protected Result<Filter.Config<?>> parseNode(Node node, String name, String value) throws ParserException {
        BaseFilterParser<?> parser = this.nested.parse(name);
        if (parser == null) {
            throw this.fail(node, null, name, "Unknown filter type");
        }

        return Result.fine(node, name, value, parser.parse(node).orFail());
    }

    //
    // Nested Parsers
    //

    @Silent
    @NestedParserName("abstain")
    @Produces(StaticFilter.Config.class)
    public static class Abstain extends BaseFilterParser<StaticFilter.Config> {
        @Override
        protected Result<StaticFilter.Config> parseNode(Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, value, StaticFilter.ABSTAIN.config());
        }
    }

    @Silent
    @NestedParserName({"allow", "true"})
    @Produces(StaticFilter.Config.class)
    public static class Allow extends BaseFilterParser<StaticFilter.Config> {
        @Override
        protected Result<StaticFilter.Config> parseNode(Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, value, StaticFilter.ALLOW.config());
        }
    }

    @Silent
    @NestedParserName({"deny", "false"})
    @Produces(StaticFilter.Config.class)
    public static class Deny extends BaseFilterParser<StaticFilter.Config> {
        @Override
        protected Result<StaticFilter.Config> parseNode(Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, value, StaticFilter.DENY.config());
        }
    }

    @Silent
    @NestedParserName("mob")
    @Produces(EntityMatcher.Config.class)
    public static class Mob extends BaseMatcherParser<EntityMatcher.Config>
                            implements InstallableParser {
        private Parser<EntityType> entityParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.entityParser = context.type(EntityType.class);
        }

        @Override
        protected Result<EntityMatcher.Config> parseNode(Node node, String name, String value) throws ParserException {
            EntityType entity = this.entityParser.parseWithDefinition(node, name, value).orFail();
            if (!entity.isAlive()) {
                throw this.fail(node, name, value, "Unknown mob type");
            }

            return Result.fine(node, name, value, new EntityMatcher.Config() {
                public Ref<EntityType> value() { return Ref.ofProvided(entity); }
            });
        }
    }

    @Silent
    @NestedParserName({"observing", "watching"})
    @Produces(ParticipatingMatcher.Config.class)
    public static class Observing extends BaseMatcherParser<ParticipatingMatcher.Config> {
        @Override
        protected Result<ParticipatingMatcher.Config> parseNode(Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, value, new ParticipatingMatcher.Config() {
                public Ref<Boolean> value() { return Ref.ofProvided(false); }
            });
        }
    }
}
