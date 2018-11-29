package pl.themolka.arcade.bossbar;

import org.bukkit.DyeColor;
import org.bukkit.boss.BarColor;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(BarColor.class)
public class BarColorParser extends ElementParser<BarColor>
                            implements InstallableParser {
    private Parser<BarColor> colorParser;
    private Parser<DyeColor> dyeParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.colorParser = context.enumType(BarColor.class);
        this.dyeParser = context.type(DyeColor.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("boss bar color");
    }

    @Override
    protected Result<BarColor> parseElement(Element element, String name, String value) throws ParserException {
        DyeColor dye = this.dyeParser.parseWithDefinition(element, name, value).orNull();
        if (dye != null) {
            BarColor result = BossBarUtils.color(dye);

            if (result != null) {
                return Result.fine(element, name, value, result);
            }
        }

        return Result.fine(element, name, value, this.colorParser.parseWithDefinition(element, name, value).orFail());
    }
}
