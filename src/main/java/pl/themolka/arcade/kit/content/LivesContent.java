package pl.themolka.arcade.kit.content;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.life.LivesGame;
import pl.themolka.arcade.life.LivesModule;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class LivesContent implements RemovableKitContent<Integer> {
    public static final int DEFAULT_COUNT = +1;

    private final int result;

    public LivesContent(int result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void attach(GamePlayer player, Integer value) {
        LivesGame module = (LivesGame) player.getGame().getModule(LivesModule.class);
        if (module != null && module.isEnabled()) {
            module.addLives(player, value);
        }
    }

    @Override
    public Integer defaultValue() {
        return DEFAULT_COUNT;
    }

    @Override
    public Integer getResult() {
        return this.result;
    }

    public static class LegacyParser implements LegacyKitContentParser<LivesContent> {
        @Override
        public LivesContent parse(Element xml) throws DataConversionException {
            try {
                return new LivesContent(Integer.parseInt(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    @NestedParserName({"lives", "life"})
    @Produces(LivesContent.class)
    public static class ContentParser extends BaseRemovableContentParser<LivesContent>
                                      implements InstallableParser {
        private Parser<Integer> livesParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.livesParser = context.type(Integer.class);
        }

        @Override
        protected ParserResult<LivesContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new LivesContent(DEFAULT_COUNT));
            }

            int lives = this.livesParser.parse(node).orFail();
            if (lives == 0) {
                throw this.fail(node, name, value, "No lives to increment or decrement");
            }

            return ParserResult.fine(node, name, value, new LivesContent(lives));
        }
    }
}
