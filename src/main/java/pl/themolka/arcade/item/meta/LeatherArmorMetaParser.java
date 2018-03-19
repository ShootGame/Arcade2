package pl.themolka.arcade.item.meta;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(LeatherArmorMeta.class)
class LeatherArmorMetaParser extends ItemMetaParser.Nested<LeatherArmorMeta> {
    private Parser<Color> colorParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.colorParser = context.type(Color.class);
    }

    @Override
    public LeatherArmorMeta parse(Node root, ItemStack itemStack, LeatherArmorMeta itemMeta) throws ParserException {
        Node node = root.firstChild("leather-armor", "leather", "armor");
        if (node != null) {
            Property color = root.property("color");
            if (color != null) {
                itemMeta.setColor(this.colorParser.parse(color).orFail());
            }
        }

        return itemMeta;
    }
}
