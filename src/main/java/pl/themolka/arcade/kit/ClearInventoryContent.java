package pl.themolka.arcade.kit;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class ClearInventoryContent implements KitContent<Boolean> {
    private final boolean result;

    public ClearInventoryContent(boolean result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.getPlayer().clearInventory(this.getResult());
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<ClearInventoryContent> {
        @Override
        public ClearInventoryContent parse(Element xml) throws DataConversionException {
            boolean armor = XMLParser.parseBoolean(xml.getAttributeValue("armor"), false);
            return new ClearInventoryContent(armor);
        }
    }
}
