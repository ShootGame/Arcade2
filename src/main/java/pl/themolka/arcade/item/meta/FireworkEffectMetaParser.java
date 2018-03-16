package pl.themolka.arcade.item.meta;

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;

@Produces(FireworkEffectMeta.class)
class FireworkEffectMetaParser extends ItemMetaParser.Nested<FireworkEffectMeta> {
    private Parser<FireworkEffect> fireworkEffectParser;

    @Override
    public void install(ParserContext context) {
        this.fireworkEffectParser = context.type(FireworkEffect.class);
    }

    @Override
    public FireworkEffectMeta parse(Node root, ItemStack itemStack, FireworkEffectMeta itemMeta) throws ParserException {
        Node node = root.firstChild("firework-effect", "fireworkeffect");
        if (node != null) {
            itemMeta.setEffect(this.fireworkEffectParser.parse(node).orFail());
        }

        return itemMeta;
    }
}
