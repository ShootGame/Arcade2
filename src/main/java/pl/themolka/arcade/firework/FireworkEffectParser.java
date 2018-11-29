package pl.themolka.arcade.firework;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(FireworkEffect.class)
public class FireworkEffectParser extends NodeParser<FireworkEffect>
                                  implements InstallableParser {
    private Parser<FireworkEffect.Type> typeParser;
    private Parser<Boolean> flickerParser;
    private Parser<Boolean> trailParser;
    private Parser<Color> colorParser;
    private Parser<Color> fadeColorParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.typeParser = context.type(FireworkEffect.Type.class);
        this.flickerParser = context.type(Boolean.class);
        this.trailParser = context.type(Boolean.class);
        this.colorParser = context.type(Color.class);
        this.fadeColorParser = context.type(Color.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("firework effect");
    }

    @Override
    protected Result<FireworkEffect> parseTree(Node node, String name) throws ParserException {
        FireworkEffect.Builder builder = FireworkEffect.builder()
                .with(this.typeParser.parse(node.property("type", "of")).orDefault(FireworkEffect.Type.BALL))
                .flicker(this.flickerParser.parse(node.property("flicker")).orDefault(false))
                .trail(this.trailParser.parse(node.property("trail")).orDefault(false));

        boolean colorsDefined = false;
        for (Node color : node.children("color")) {
            builder.withColor(this.colorParser.parse(color).orFail());
            colorsDefined = true;
        }

        if (!colorsDefined) {
            throw this.fail(node, name, null, "No colors defined");
        }

        for (Node fadeColor : node.children("fade-color", "fadecolor", "fade")) {
            builder.withFade(this.fadeColorParser.parse(fadeColor).orFail());
        }

        return Result.fine(node, name, builder.build());
    }
}
