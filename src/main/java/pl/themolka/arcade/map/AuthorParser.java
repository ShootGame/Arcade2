package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Produces(Author.class)
public class AuthorParser extends NodeParser<Author>
                          implements InstallableParser {
    private Parser<String> usernameParser;
    private Parser<UUID> uuidParser;
    private Parser<String> descriptionParser;

    @Override
    public void install(ParserContext context) {
        this.usernameParser = context.text();
        this.uuidParser = context.type(UUID.class);
        this.descriptionParser = context.text();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("an author");
    }

    @Override
    protected ParserResult<Author> parsePrimitive(Node node, String name, String value) throws ParserException {
        UUID uuid = this.uuidParser.parse(node.property("uuid", "uid")).orDefaultNull();
        String username = this.usernameParser.parseWithDefinition(node, name, value).orFail(); // required
        String description = this.descriptionParser.parse(node.property("description", "contribution")).orDefaultNull();
        return ParserResult.fine(node, name, value, Author.of(uuid, username, description));
    }
}
