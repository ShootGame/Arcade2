package pl.themolka.arcade.score;

import org.jdom2.Element;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.kit.KitsGame;
import pl.themolka.arcade.portal.XMLPortal;
import pl.themolka.arcade.spawn.SpawnsGame;
import pl.themolka.arcade.xml.XMLParser;

public class XMLScoreBox extends XMLParser {
    public static ScoreBox parse(Game game, Element xml, ScoreBox scoreBox,
                                 FiltersGame filters, KitsGame kits, SpawnsGame spawns) {
        return (ScoreBox) XMLPortal.parse(game, xml, scoreBox, filters, kits, spawns);
    }
}
