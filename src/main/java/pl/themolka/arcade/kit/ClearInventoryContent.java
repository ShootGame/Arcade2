package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
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
    public void apply(GamePlayer player) {
        if (player.isOnline()) {
            player.getPlayer().clearInventory(this.getResult());
        }
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<ClearInventoryContent> {
        @Override
        public ClearInventoryContent parse(Element xml) throws DataConversionException {
            Attribute armor = xml.getAttribute("armor");
            return new ClearInventoryContent(armor != null && XMLParser.parseBoolean(armor.getValue()));
        }
    }
}
