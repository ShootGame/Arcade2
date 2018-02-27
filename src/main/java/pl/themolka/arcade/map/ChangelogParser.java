package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Produces(Changelog.class)
public class ChangelogParser extends NodeParser<Changelog>
                             implements InstallableParser {
    private Parser<MapVersion> versionParser;
    private Parser<LocalDate> releaseParser;
    private Parser<String> logParser;

    @Override
    public void install(ParserContext context) {
        this.versionParser = context.type(MapVersion.class);
        this.releaseParser = context.type(LocalDate.class);
        this.logParser = context.text();
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a changelog");
    }

    @Override
    protected ParserResult<Changelog> parseTree(Node node, String name) throws ParserException {
        MapVersion version = this.versionParser.parse(node.property("version")).orFail();
        LocalDate release = this.releaseParser.parse(node.property("release")).orDefaultNull();

        Changelog result = new Changelog(version, release);
        result.addAll(this.parseLogs(node));
        return ParserResult.fine(node, name, result);
    }

    private List<String> parseLogs(Node node) throws ParserException {
        List<String> logs = new ArrayList<>();
        for (Node log : node.children()) {
            String text = this.logParser.parse(log).orDefaultNull();

            if (text != null) {
                logs.add(text);
            }
        }

        return logs;
    }
}
