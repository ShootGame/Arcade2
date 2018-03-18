package pl.themolka.arcade.life;

import org.bukkit.Sound;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.xml.XMLParser;
import pl.themolka.arcade.xml.XMLSound;

@ModuleInfo(id = "Lives",
        dependency = {
                MatchModule.class})
public class LivesModule extends Module<LivesGame> {
    @Override
    public LivesGame buildGameModule(Element xml, Game game) throws JDOMException {
        int lives = XMLParser.parseInt(xml.getValue(), 1);
        if (lives > 0) {
            boolean announce = XMLParser.parseBoolean(xml.getAttributeValue("announce"), true);
            Sound sound = XMLSound.parse(xml.getAttributeValue("sound"), LivesGame.DEFAULT_SOUND);

            return new LivesGame(lives, announce, sound);
        }

        return null;
    }

    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) {
        return context.of(LivesGameParser.class);
    }
}
