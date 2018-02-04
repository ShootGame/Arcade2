package pl.themolka.arcade.kit;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.life.LivesGame;
import pl.themolka.arcade.life.LivesModule;

public class LivesContent implements RemovableKitContent<Integer> {
    public static final int DEFAULT_COUNT = +1;

    private final int result;

    public LivesContent(int result) {
        this.result = result;
    }

    @Override
    public void attach(GamePlayer player, Integer value) {
        GameModule module = player.getGame().getModule(LivesModule.class);
        if (module != null && module.isEnabled() && module instanceof LivesGame) {
            ((LivesGame) module).addLives(player, value);
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

    public static class Parser implements KitContentParser<LivesContent> {
        @Override
        public LivesContent parse(Element xml) throws DataConversionException {
            try {
                return new LivesContent(Integer.parseInt(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
