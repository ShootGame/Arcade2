package pl.themolka.arcade.kit;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.kit.content.KitContent;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Produces(Kit.Config.class)
public class KitParser extends ConfigParser<Kit.Config>
                       implements InstallableParser {
    private Parser<KitContent> contentParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("kit");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.contentParser = context.type(KitContent.class);
    }

    @Override
    protected ParserResult<Kit.Config> parseTree(Node node, String name) throws ParserException {
        String id = this.parseRequiredId(node);

        List<KitContent<?>> contents = new ArrayList<>();
        for (Node contentNode : node.children()) {
            contents.add(this.contentParser.parse(contentNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(contents)) {
            throw this.fail(node, name, null, "No kit contents defined");
        }

        Set<String> inherit = new LinkedHashSet<>();
        String inheritValue = node.propertyValue("inherit", "parent", "parents");
        if (inheritValue != null) {
            inherit.addAll(ParserUtils.array(inheritValue));
        }

        return ParserResult.fine(node, name, new Kit.Config() {
            public String id() { return id; }
            public List<KitContent<?>> contents() { return contents; }
            public Set<String> inherit() { return inherit; }
        });
    }
}
