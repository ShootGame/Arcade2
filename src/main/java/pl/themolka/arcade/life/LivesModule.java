package pl.themolka.arcade.life;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.xml.XMLParser;

@ModuleInfo(id = "Lives",
        dependency = {
                MatchModule.class})
public class LivesModule extends Module<LivesGame> {
    @Override
    public LivesGame buildGameModule(Element xml, Game game) throws JDOMException {
        int lives;
        try {
            lives = Integer.parseInt(xml.getValue());
        } catch (NumberFormatException ex) {
            lives = 1;
        }

        if (lives > 0) {
            return new LivesGame(lives,
                                 null,
                                 XMLParser.parseBoolean(xml.getAttributeValue("announce"), true),
                                 LivesGame.DEFAULT_SOUND);
        }

        return null;
    }

    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(LivesGameParser.class);
    }
}
