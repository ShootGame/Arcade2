package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.Nulls;

import java.util.Collections;
import java.util.Set;

@Produces(Observers.Config.class)
public class ObserversParser extends ConfigParser<Observers.Config>
                             implements InstallableParser {
    private Parser<Color> colorParser;
    private Parser<String> nameParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.colorParser = context.type(Color.class);
        this.nameParser = context.type(String.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("observers team");
    }

    @Override
    protected ParserResult<Observers.Config> parseNode(Node node, String name, String value) throws ParserException {
        Color color = this.colorParser.parse(node.property("color")).orFail();
        String observersName = this.nameParser.parse(node.property("name", "title")).orFail();

        return ParserResult.fine(node, name, value, new Observers.Config() {
            public ChatColor chatColor() { return Nulls.defaults(color.toChat(), Observers.OBSERVERS_CHAT_COLOR); }
            public DyeColor dyeColor() { return Nulls.defaults(color.toDye(), Observers.OBSERVERS_DYE_COLOR); }
            public String name() { return observersName; }
        });
    }
}