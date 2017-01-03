package pl.themolka.arcade.kit;

import org.bukkit.GameMode;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLGameMode;

public class GameModeContent implements KitContent<GameMode>  {
    private final GameMode result;

    public GameModeContent(GameMode result) {
        this.result = result;
    }

    @Override
    public void apply(GamePlayer player) {
        player.getPlayer().getBukkit().setGameMode(this.getResult());
    }

    @Override
    public GameMode getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<GameModeContent> {
        @Override
        public GameModeContent parse(Element xml) throws DataConversionException {
            return new GameModeContent(XMLGameMode.parse(xml.getValue()));
        }
    }
}
