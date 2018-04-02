package pl.themolka.arcade.parser.type;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Pattern.class)
public class BannerPatternParser extends NodeParser<Pattern>
                                 implements InstallableParser {
    private Parser<PatternType> typeParser;
    private Parser<DyeColor> colorParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.typeParser = context.type(PatternType.class);
        this.colorParser = context.type(DyeColor.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("banner pattern");
    }

    @Override
    protected ParserResult<Pattern> parsePrimitive(Node node, String name, String value) throws ParserException {
        PatternType type = this.typeParser.parseWithDefinition(node, name, value).orFail();
        DyeColor color = this.colorParser.parse(node.property("color")).orFail();
        return ParserResult.fine(node, name, value, new Pattern(color, type));
    }
}
