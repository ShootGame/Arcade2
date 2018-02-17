package pl.themolka.arcade.kit;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;

public class KillContent implements BaseVoidKitContent {
    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player) && !player.isDead();
    }

    @Override
    public void apply(GamePlayer player) {
        player.kill();
    }

    public static class Parser implements KitContentParser<KillContent> {
        @Override
        public KillContent parse(Element xml) throws DataConversionException {
            return new KillContent();
        }
    }
}
