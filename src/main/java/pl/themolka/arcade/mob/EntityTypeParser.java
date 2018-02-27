package pl.themolka.arcade.mob;

import org.bukkit.entity.EntityType;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.List;

@Produces(EntityType.class)
public class EntityTypeParser extends ElementParser<EntityType>
                              implements InstallableParser {
    private Parser<EntityType> entityTypeParser;
    private Parser<String> textParser;

    @Override
    public void install(ParserContext context) {
        this.entityTypeParser = context.enumType(EntityType.class);
        this.textParser = context.text();
    }

    @Override
    public List<Object> expect() {
        return this.entityTypeParser.expect();
    }

    @Override
    protected ParserResult<EntityType> parseElement(Element element, String name, String value) throws ParserException {
        EntityType entityType = EntityType.fromName(this.parseEntityName(element, name, value));
        if (entityType != null) {
            return ParserResult.fine(element, name, value, entityType);
        }

        return this.entityTypeParser.parseWithDefinition(element, name, value);
    }

    private String parseEntityName(Element element, String name, String value) {
        return this.textParser.parseWithDefinition(element, name, EnumParser.toEnumValue(value)).orNull();
    }
}
