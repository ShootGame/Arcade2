package pl.themolka.arcade.filter.matcher;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityEvent;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class EntityMatcher extends ConfigurableMatcher<EntityType> {
    protected EntityMatcher(Config config) {
        super(config.value().get());
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof EntityType) {
            return this.matches((EntityType) object);
        } else if (object instanceof Entity) {
            return this.matches((Entity) object);
        } else if (object instanceof EntityEvent) {
            return this.matches((EntityEvent) object);
        }

        return false;
    }

    public boolean matches(Entity entity) {
        return entity != null && this.matches(entity.getType());
    }

    public boolean matches(EntityEvent event) {
        return event != null && this.matches(event.getEntityType());
    }

    @NestedParserName("entity")
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config>
                                      implements InstallableParser {
        private Parser<EntityType> entityParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.entityParser = context.type(EntityType.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            EntityType entity = this.entityParser.parseWithDefinition(node, name, value).orFail();

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<EntityType> value() { return Ref.ofProvided(entity); }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<EntityMatcher, EntityType> {
        @Override
        default EntityMatcher create(Game game) {
            return new EntityMatcher(this);
        }
    }
}
