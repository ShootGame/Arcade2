package pl.themolka.arcade.item.meta;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(SkullMeta.class)
class SkullMetaParser extends ItemMetaParser.Nested<SkullMeta>
                      implements InstallableParser {
    private Parser<String> ownerParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.ownerParser = context.text();
    }

    @Override
    public SkullMeta parse(Node root, ItemStack itemStack, SkullMeta itemMeta) throws ParserException {
        Node node = root.firstChild("skull", "head");
        if (node != null) {
            Property owner = node.property("owner", "of");
            if (owner != null) {
                itemMeta.setOwner(this.ownerParser.parse(owner).orFail());
            }
        }

        return itemMeta;
    }
}
