package pl.themolka.arcade.parser.type;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(BaseComponent.class)
public class ComponentParser extends ElementParser<BaseComponent>
                             implements InstallableParser {
    private Parser<String> messageParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("chat component");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.messageParser = context.type(String.class);
    }

    @Override
    protected ParserResult<BaseComponent> parseElement(Element element, String name, String value) throws ParserException {
        BaseComponent[] text = TextComponent.fromLegacyText(this.messageParser.parse(element).orFail());
        return ParserResult.fine(element, name, value, new TextComponent(text));
    }
}
