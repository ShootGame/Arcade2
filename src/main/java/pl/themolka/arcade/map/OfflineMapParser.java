package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Produces(OfflineMap.class)
public class OfflineMapParser extends NodeParser<OfflineMap>
                              implements InstallableParser {
    private Parser<String> nameParser;
    private Parser<MapVersion> versionParser;
    private Parser<String> descriptionParser;
    private Parser<Author> authorParser;
    private Parser<Changelog> changelogParser;

    @Override
    public void install(ParserContext context) {
        this.nameParser = context.text();
        this.versionParser = context.type(MapVersion.class);
        this.descriptionParser = context.type(String.class); // can be colored!
        this.authorParser = context.type(Author.class);
        this.changelogParser = context.type(Changelog.class);
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("basic map information");
    }

    @Override
    protected ParserResult<OfflineMap> parseTree(Node node, String name) throws ParserException {
        String mapName = this.nameParser.parse(node.firstChild("name")).orNull(); // we handle orFail() manually
        MapVersion version = this.versionParser.parse(node.firstChild("version")).orNull(); // we handle orFail() manually
        String description = this.descriptionParser.parse(node.firstChild("description", "objective")).orNull();

        if (mapName == null || version == null) {
            throw this.fail(node, name, null, "<name> and <version> are required in all map manifests!");
        }

        int mapNameLength = mapName.length();
        if (mapNameLength < OfflineMap.NAME_MIN_LENGTH) {
            throw this.fail(node, "name", "Map name is shorter than " + OfflineMap.NAME_MIN_LENGTH + " characters");
        } else if (mapNameLength > OfflineMap.NAME_MAX_LENGTH) {
            throw this.fail(node, "name", "Map name is longer than " + OfflineMap.NAME_MAX_LENGTH + " characters");
        }

        List<Author> authors = this.parseAuthors(node);
        Node authorsNode = node.firstChild("authors", "contributors", "teams");
        if (authorsNode != null) {
            authors.addAll(this.parseAuthors(authorsNode));
        }

        List<Changelog> changelogs = new ArrayList<>();
        Node changelogsNode = node.firstChild("changelog", "changes");
        if (changelogsNode != null) {
            changelogs.addAll(this.parseChangelogs(changelogsNode));
        }

        return ParserResult.fine(node, name, new OfflineMap(mapName, version, description, authors, changelogs));
    }

    private List<Author> parseAuthors(Node node) throws ParserException {
        List<Author> authors = new ArrayList<>();
        for (Node author : node.children("author", "contributor", "team")) {
            authors.add(this.authorParser.parse(author).orFail());
        }

        return authors;
    }

    private List<Changelog> parseChangelogs(Node node) throws ParserException {
        List<Changelog> changelogs = new ArrayList<>();
        for (Node changelog : node.children()) {
            changelogs.add(this.changelogParser.parse(changelog).orFail());
        }

        return changelogs;
    }
}
