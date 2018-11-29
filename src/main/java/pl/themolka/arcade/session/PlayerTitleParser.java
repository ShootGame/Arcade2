package pl.themolka.arcade.session;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
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

import java.util.Collections;
import java.util.Set;

@Produces(PlayerTitle.class)
public class PlayerTitleParser extends NodeParser<PlayerTitle>
                               implements InstallableParser {
    private static final BaseComponent empty = new TextComponent();

    private Parser<BaseComponent> primaryParser;
    private Parser<BaseComponent> secondaryParser;
    private Parser<Time> fadeInParser;
    private Parser<Time> viewTimeParser;
    private Parser<Time> fadeOutParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("player title");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.primaryParser = context.type(BaseComponent.class);
        this.secondaryParser = context.type(BaseComponent.class);
        this.fadeInParser = context.type(Time.class);
        this.viewTimeParser = context.type(Time.class);
        this.fadeOutParser = context.type(Time.class);
    }

    @Override
    protected Result<PlayerTitle> parseTree(Node node, String name) throws ParserException {
        BaseComponent primary = this.primaryParser.parse(node.firstChild("title", "primary")).orDefaultNull();
        BaseComponent secondary = this.secondaryParser.parse(node.firstChild("subtitle", "sub-title", "secondary")).orDefaultNull();

        if (primary == null && secondary == null) {
            return Result.empty(node, "Missing <title> or <subtitle>");
        }

        PlayerTitle title = new PlayerTitle(primary != null ? primary : empty, secondary != null ? secondary : empty);

        Time fadeIn = this.fadeInParser.parse(node.property("fade-in", "fadein")).orDefaultNull();
        if (fadeIn != null) {
            title.setFadeIn(fadeIn);
        }

        Time viewTime = this.viewTimeParser.parse(node.property("view-time", "viewtime", "time", "timeout")).orDefaultNull();
        if (viewTime != null) {
            title.setViewTime(viewTime);
        }

        Time fadeOut = this.fadeOutParser.parse(node.property("fade-out", "fadeout")).orDefaultNull();
        if (fadeOut != null) {
            title.setFadeOut(fadeOut);
        }

        return Result.fine(node, name, title);
    }
}
