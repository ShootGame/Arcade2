package pl.themolka.arcade.potion;

import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.time.TimeUtils;

import java.util.Collections;
import java.util.Set;

@Produces(PotionEffect.class)
public class PotionEffectParser extends NodeParser<PotionEffect>
                                implements InstallableParser {
    private Parser<Boolean> ambientParser;
    private Parser<Integer> amplifierParser;
    private Parser<Color> colorParser;
    private Parser<Time> durationParser;
    private Parser<Boolean> particlesParser;
    private Parser<PotionEffectType> typeParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.ambientParser = context.type(Boolean.class);
        this.amplifierParser = context.type(Integer.class);
        this.colorParser = context.type(Color.class);
        this.durationParser = context.type(Time.class);
        this.particlesParser = context.type(Boolean.class);
        this.typeParser = context.type(PotionEffectType.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("potion effect");
    }

    @Override
    protected Result<PotionEffect> parsePrimitive(Node node, String name, String value) throws ParserException {
        boolean ambient = this.ambientParser.parse(node.property("ambient")).orDefault(true);
        int amplifier = this.amplifierParser.parse(node.property("amplifier")).orDefault(0);
        Color color = this.colorParser.parse(node.property("color")).orDefaultNull();
        Time duration = this.durationParser.parse(node.property("duration", "time")).orFail();
        boolean particles = this.particlesParser.parse(node.property("particles")).orDefault(true);
        PotionEffectType type = this.typeParser.parseWithDefinition(node, name, value).orFail();

        if (amplifier < 0) {
            throw this.fail(node, name, value, "Amplifier must be positive or zero (greater than, or equal to 0)");
        }

        return Result.fine(node, name, value, new PotionEffect(
                type, TimeUtils.toTicksInt(duration), amplifier, ambient, particles, color));
    }
}
