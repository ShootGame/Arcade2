package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Produces(OfflineMap.class)
public class OfflineMapParser extends NodeParser<OfflineMap>
                              implements InstallableParser {
    public static final String MISSING_NAME_VERSION = "Missing <name> and <version> elements. " +
            "<name> and <version> are required in all map manifests!";

    private Parser<MapFileVersion> fileVersionParser;
    private Parser<String> nameParser;
    private Parser<MapVersion> versionParser;
    private Parser<String> descriptionParser;
    private Parser<Author> authorParser;
    private Parser<Changelog> changelogParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.fileVersionParser = context.type(MapFileVersion.class);
        this.nameParser = context.text();
        this.versionParser = context.type(MapVersion.class);
        this.descriptionParser = context.type(String.class); // can be colored!
        this.authorParser = context.type(Author.class);
        this.changelogParser = context.type(Changelog.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("basic map information");
    }

    @Override
    protected ParserResult<OfflineMap> parseTree(Node node, String name) throws ParserException {
        Property fileVersionProperty = node.property("fileversion", "file-version", "ver", "version", "proto", "manifest");
        MapFileVersion fileVersion = this.fileVersionParser.parse(fileVersionProperty).orDefault(MapFileVersions.NEWEST);

        Node nameNode = node.firstChild("name");
        Node versionNode = node.firstChild("version", "ver");
        if (nameNode == null || versionNode == null) {
            throw new ParserException(node, MISSING_NAME_VERSION);
        }

        String mapName = this.nameParser.parse(nameNode).orFail();
        MapVersion version = this.versionParser.parse(versionNode).orFail();
        String description = this.descriptionParser.parse(node.firstChild("description", "objective", "goal", "about")).orNull();

        int mapNameLength = mapName.length();
        if (mapNameLength < OfflineMap.NAME_MIN_LENGTH) {
            throw this.fail(nameNode, nameNode.getName(), nameNode.getValue(),
                    "Map name is shorter than " + OfflineMap.NAME_MIN_LENGTH + " characters");
        } else if (mapNameLength > OfflineMap.NAME_MAX_LENGTH) {
            throw this.fail(nameNode, nameNode.getName(), nameNode.getValue(),
                    "Map name is longer than " + OfflineMap.NAME_MAX_LENGTH + " characters");
        }

        List<Author> authors = this.parseAuthors(node);
        Node authorsNode = node.firstChild("authors", "contributors", "teams");
        if (authorsNode != null) {
            authors.addAll(this.parseAuthors(authorsNode));
        }

        List<Changelog> changelogs = new ArrayList<>();
        Node changelogsNode = node.firstChild("changelog", "change-log", "changes");
        if (changelogsNode != null) {
            changelogs.addAll(this.parseChangelogs(changelogsNode));
        }

        return ParserResult.fine(node, name, new OfflineMap(fileVersion,
                                                            mapName,
                                                            version,
                                                            description,
                                                            authors,
                                                            changelogs));
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
